import { Component, NgZone, ViewEncapsulation } from '@angular/core'
import { CommonModule } from '@angular/common'
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms'
import { MatTableModule } from '@angular/material/table'
import { MatButtonModule } from '@angular/material/button'
import { MatFormFieldModule } from '@angular/material/form-field'
import { MatInputModule } from '@angular/material/input'
import { MatIconModule } from '@angular/material/icon'
import { MeasurementService } from '../../services/measurement.service'
import { Observable, BehaviorSubject } from 'rxjs'
import { map, startWith, filter, defaultIfEmpty, catchError, tap } from 'rxjs/operators'
import { Measurement } from '../../services/measurement.service'
import { PATIENT_ID_VALIDATORS, RESULT_VALIDATORS } from '../../validators/measurement-validators'
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar'
import { SNACKBAR_MESSAGES } from '../../utils/snackbar-messages'

@Component({
    selector: 'app-measurement-list',
    standalone: true,
    encapsulation: ViewEncapsulation.None,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatTableModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule
    ],
    templateUrl: './measurement-list.component.html',
    styleUrls: ['./measurement-list.component.scss']
})
export class MeasurementListComponent {
    measurements$ = new BehaviorSubject<Measurement[]>([])
    filteredMeasurements$: Observable<Measurement[]>
    searchId = ''
    editableRow: string | null = null
    editForms: Record<string, FormGroup> = {}
    measurementForm: FormGroup
    showAddMeasurement = false
    successMessage: string | null = null
    isErrorMessage: boolean = false

    // Lifecycle Hooks
    constructor(
        private measurementService: MeasurementService,
        protected fb: FormBuilder,
        private zone: NgZone,
        private snackBar: MatSnackBar
    ) {
        this.filteredMeasurements$ = this.measurements$.pipe(
            map(measurements => measurements || []),
            filter(Array.isArray),
            startWith([]),
            defaultIfEmpty([])
        )

        this.measurementForm = this.fb.group({
            patientId: ['', PATIENT_ID_VALIDATORS],
            result: ['', RESULT_VALIDATORS]
        })
    }

    // Public Methods
    async fetchAllMeasurements(): Promise<void> {
        try {
            const data = await this.measurementService.getAllMeasurements().toPromise()
            if (data) {
                this.measurements$.next(data)
                this.initializeEditForms(data)
                this.showAddMeasurement = false
                this.editableRow = null
            }
        } catch (error) {
            this.handleError(SNACKBAR_MESSAGES.FETCH_ERROR, error)
        }
    }

    toggleAddMeasurement(): void {
        this.showAddMeasurement = !this.showAddMeasurement

        // Reset the form for the currently edited row, if any
        if (this.editableRow) {
            this.resetFormToOriginalValues(this.editableRow)
        }

        this.editableRow = null
    }

    editMeasurement(uuid: string): void {
        // Restore the initial value of the currently edited row, if any
        if (this.editableRow && this.editForms[this.editableRow]) {
            this.resetFormToOriginalValues(this.editableRow)
        }

        // Set the new row as editable
        this.editableRow = uuid
        this.showAddMeasurement = false
    }

    async saveMeasurement(measurement: Measurement): Promise<void> {
        if (!measurement.uuid) {
            this.showSnackBar(SNACKBAR_MESSAGES.FORM_ERROR)
            return
        }
        const form = this.editForms[measurement.uuid]
        if (!form || form.invalid) {
            this.showSnackBar(SNACKBAR_MESSAGES.FORM_ERROR)
            return
        }

        const updatedMeasurement: Measurement = {
            ...measurement,
            ...form.value
        }

        this.measurementService
            .updateMeasurement(updatedMeasurement)
            .pipe(
                tap((updated: Measurement) => {
                    this.updateMeasurementList(updated)
                    this.showSnackBar(SNACKBAR_MESSAGES.UPDATE_SUCCESS)
                }),
                catchError(error => {
                    this.handleError(SNACKBAR_MESSAGES.UPDATE_ERROR, error)
                    return []
                })
            )
            .subscribe()
    }

    async deleteMeasurement(uuid: string): Promise<void> {
        this.measurementService
            .deleteMeasurementByUuid(uuid)
            .pipe(
                tap(() => {
                    const measurements = this.measurements$.getValue().filter(m => m.uuid !== uuid)
                    this.measurements$.next(measurements)
                    this.showSnackBar(SNACKBAR_MESSAGES.DELETE_SUCCESS)
                }),
                catchError(error => {
                    this.handleError(SNACKBAR_MESSAGES.DELETE_ERROR, error)
                    return []
                })
            )
            .subscribe()
    }

    onSubmit(): void {
        if (this.measurementForm.invalid) {
            this.showSnackBar(SNACKBAR_MESSAGES.FORM_ERROR)
            return
        }

        const newMeasurement = this.measurementForm.value

        this.handleMeasurementServiceResponse(
            this.measurementService.addMeasurement(newMeasurement),
            SNACKBAR_MESSAGES.ADD_SUCCESS,
            (addedMeasurement) => {
                if (addedMeasurement.uuid) {
                    const currentMeasurements = this.measurements$.getValue()
                    this.measurements$.next([...currentMeasurements, addedMeasurement])

                    this.editForms[addedMeasurement.uuid] = this.createEditForm(addedMeasurement)

                    this.showAddMeasurement = false
                    this.measurementForm.reset()
                } else {
                    this.handleError('UUID is undefined for the added measurement', new Error('UUID is undefined'))
                }
            }
        )
    }

    onCancel(): void {
        this.showAddMeasurement = false
        if (this.editableRow) {
            this.resetFormToOriginalValues(this.editableRow)
        }
        this.editableRow = null
    }

    searchById(): void {
        this.filteredMeasurements$ = this.measurements$.pipe(
            map(measurements => measurements.filter(measurement =>
                this.searchId ? measurement.patientId.toString().includes(this.searchId) : true
            )),
            startWith([])
        )
    }

    getMeasurementByUuid(uuid: string | null): Measurement | null {
        if (!uuid) return null
        return this.measurements$.getValue().find(m => m.uuid === uuid) || null
    }

    // Private Methods
    private initializeEditForms(data: Measurement[]): void {
        this.editForms = {}
        data.forEach(measurement => {
            if (measurement.uuid !== undefined) {
                this.editForms[measurement.uuid] = this.createEditForm(measurement)
            }
        })
    }

    private updateMeasurementList(updatedMeasurement: Measurement): void {
        const measurements = this.measurements$.getValue()
        const index = measurements.findIndex(m => m.uuid === updatedMeasurement.uuid)
        if (index !== -1) {
            measurements[index] = updatedMeasurement
            this.measurements$.next([...measurements])
            this.editableRow = null
        }
    }

    private fetchAndAddMeasurement(uuid: string): void {
        this.measurementService.getMeasurementByUuid(uuid).subscribe(
            fetchedMeasurement => this.addFetchedMeasurement(fetchedMeasurement),
            error => this.handleError('Error fetching the measurement', error)
        )
    }

    private addFetchedMeasurement(fetchedMeasurement: Measurement): void {
        this.zone.run(() => {
            const measurements = this.measurements$.getValue()
            this.measurements$.next([...measurements, fetchedMeasurement])

            if (fetchedMeasurement.uuid !== undefined) {
                this.editForms[fetchedMeasurement.uuid] = this.createEditForm(fetchedMeasurement)
            }

            this.measurementForm.reset()
            this.showAddMeasurement = false
        })
    }

    private handleMeasurementServiceResponse<T = void>(
        observable: Observable<T>,
        successMessage: string,
        onSuccess?: (response: T) => void
    ): void {
        observable.subscribe(
            response => {
                if (onSuccess) {
                    onSuccess(response)
                }
                this.showSnackBar(successMessage)
            },
            error => this.handleError(successMessage, error)
        )
    }

    // Utility Methods
    protected createEditForm(measurement: Measurement): FormGroup {
        return this.fb.group({
            patientId: [measurement.patientId, PATIENT_ID_VALIDATORS],
            result: [measurement.result, RESULT_VALIDATORS]
        })
    }

    protected showSnackBar(message: string, horizontalPosition: MatSnackBarHorizontalPosition = 'center', verticalPosition: MatSnackBarVerticalPosition = 'bottom'): void {
        this.snackBar.open(message, 'Close', {
            duration: 3000,
            horizontalPosition,
            verticalPosition
        })
    }

    protected handleError(context: string, error: unknown): void {
        console.error(`${context}:`, error)
        this.showSnackBar(SNACKBAR_MESSAGES.UNEXPECTED_ERROR)
    }

    private resetFormToOriginalValues(uuid: string): void {
        const originalMeasurement = this.getMeasurementByUuid(uuid)
        if (originalMeasurement && this.editForms[uuid]) {
            this.editForms[uuid].reset({
                patientId: originalMeasurement.patientId,
                result: originalMeasurement.result
            })
        }
    }
}
