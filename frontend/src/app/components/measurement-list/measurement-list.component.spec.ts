import { TestBed, ComponentFixture } from '@angular/core/testing'
import { MeasurementListComponent } from './measurement-list.component'
import { MeasurementService } from '../../services/measurement.service'
import { of, throwError } from 'rxjs'
import { ReactiveFormsModule, FormBuilder } from '@angular/forms'
import { MatSnackBarModule } from '@angular/material/snack-bar'
import { HttpClientTestingModule } from '@angular/common/http/testing'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { tap } from 'rxjs/operators'

describe('MeasurementListComponent', () => {
    let component: MeasurementListComponent
    let fixture: ComponentFixture<MeasurementListComponent>
    let mockMeasurementService: jasmine.SpyObj<MeasurementService>
    let formBuilder: FormBuilder

    beforeEach(async () => {
        mockMeasurementService = jasmine.createSpyObj('MeasurementService', [
            'getAllMeasurements',
            'addMeasurement',
            'updateMeasurement',
            'deleteMeasurementByUuid',
            'getMeasurementByUuid', // Added spy for getMeasurementByUuid
            'handleServiceResponse'
        ])

        mockMeasurementService.handleServiceResponse.and.callFake((observable, successMessage, onSuccess) => {
            return observable.pipe(
                tap(response => {
                    if (onSuccess) {
                        onSuccess(response)
                    }
                })
            )
        })

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                MatSnackBarModule,
                HttpClientTestingModule,
                BrowserAnimationsModule,
                MeasurementListComponent
            ],
            providers: [
                { provide: MeasurementService, useValue: mockMeasurementService }
            ]
        }).compileComponents()

        fixture = TestBed.createComponent(MeasurementListComponent)
        component = fixture.componentInstance
        formBuilder = TestBed.inject(FormBuilder)
        fixture.detectChanges()
    })

    it('should create the component', () => {
        expect(component).toBeTruthy()
    })

    it('should fetch all measurements on initialization', async () => {
        const mockMeasurements = [
            { uuid: '1', patientId: 101, result: 98.6 },
            { uuid: '2', patientId: 102, result: 99.1 }
        ]
        mockMeasurementService.getAllMeasurements.and.returnValue(of(mockMeasurements))

        await component.fetchAllMeasurements()

        expect(mockMeasurementService.getAllMeasurements).toHaveBeenCalled()
        expect(component.measurements$.getValue()).toEqual(mockMeasurements)
    })

    it('should handle errors when fetching measurements fails', async () => {
        mockMeasurementService.getAllMeasurements.and.returnValue(throwError(() => new Error('Fetch error')))

        spyOn(console, 'error')

        await component.fetchAllMeasurements()

        expect(console.error).toHaveBeenCalledWith(jasmine.any(String), jasmine.any(Error))
    })

    it('should add a new measurement', () => {
        const newMeasurement = { uuid: '3', patientId: 103, result: 97.5 }
        component.measurementForm.setValue({
            patientId: newMeasurement.patientId,
            result: newMeasurement.result
        })

        mockMeasurementService.addMeasurement.and.returnValue(of(newMeasurement))

        component.onSubmit()

        expect(mockMeasurementService.addMeasurement).toHaveBeenCalledWith({
            patientId: newMeasurement.patientId,
            result: newMeasurement.result
        })
        expect(component.measurements$.getValue()).toContain(newMeasurement)
    })

    it('should handle errors when adding a new measurement fails', () => {
        const newMeasurement = { uuid: '3', patientId: 103, result: 97.5 }
        component.measurementForm.setValue({
            patientId: newMeasurement.patientId,
            result: newMeasurement.result
        })

        mockMeasurementService.addMeasurement.and.returnValue(throwError(() => new Error('Add error')))

        spyOn(console, 'error')

        component.onSubmit()

        expect(console.error).toHaveBeenCalledWith(jasmine.any(String), jasmine.any(Error))
    })

    it('should fetch and add a measurement by UUID indirectly', async () => {
        const mockMeasurement = { uuid: '3', patientId: 103, result: 97.5 }
        mockMeasurementService.getMeasurementByUuid.and.returnValue(of(mockMeasurement))

        // Simulate a public method or behavior that triggers fetchAndAddMeasurement
        component.measurements$.next([]) // Clear existing measurements
        component['fetchAndAddMeasurement']('3') // Accessing private method for testing

        expect(mockMeasurementService.getMeasurementByUuid).toHaveBeenCalledWith('3')
        expect(component.measurements$.getValue()).toContain(mockMeasurement)
    })

    it('should handle errors when fetching a measurement by UUID fails', async () => {
        mockMeasurementService.getMeasurementByUuid.and.returnValue(throwError(() => new Error('Fetch by UUID error')))

        spyOn(console, 'error')

        await component['fetchAndAddMeasurement']('3')

        expect(console.error).toHaveBeenCalledWith(jasmine.any(String), jasmine.any(Error))
    })

    it('should delete a measurement', async () => {
        const mockUuid = '1'
        const mockMeasurements = [
            { uuid: '1', patientId: 101, result: 98.6 },
            { uuid: '2', patientId: 102, result: 99.1 }
        ]
        component.measurements$.next(mockMeasurements)
        mockMeasurementService.deleteMeasurementByUuid.and.returnValue(of(void 0))

        await component.deleteMeasurement(mockUuid)

        expect(mockMeasurementService.deleteMeasurementByUuid).toHaveBeenCalledWith(mockUuid)
        expect(component.measurements$.getValue().length).toBe(1)
    })

    it('should handle errors when deleting a measurement fails', async () => {
        const mockUuid = '1'
        mockMeasurementService.deleteMeasurementByUuid.and.returnValue(throwError(() => new Error('Delete error')))

        spyOn(console, 'error')

        await component.deleteMeasurement(mockUuid)

        expect(console.error).toHaveBeenCalledWith(jasmine.any(String), jasmine.any(Error))
    })

    it('should update a measurement', async () => {
        const mockMeasurement = { uuid: '1', patientId: 101, result: 98.6 }
        const updatedMeasurement = { uuid: '1', patientId: 101, result: 99.0 }

        component.editForms[mockMeasurement.uuid] = formBuilder.group({
            patientId: [mockMeasurement.patientId],
            result: [updatedMeasurement.result]
        })

        component.measurements$.next([mockMeasurement])

        mockMeasurementService.updateMeasurement.and.returnValue(of(updatedMeasurement))

        await component.saveMeasurement(mockMeasurement)

        expect(component.measurements$.getValue()[0]).toEqual(updatedMeasurement)
    })

    it('should handle errors when updating a measurement fails', async () => {
        const mockMeasurement = { uuid: '1', patientId: 101, result: 98.6 }
        component.editForms[mockMeasurement.uuid] = formBuilder.group({
            patientId: [mockMeasurement.patientId],
            result: [mockMeasurement.result]
        })

        mockMeasurementService.updateMeasurement.and.returnValue(throwError(() => new Error('Update error')))

        spyOn(console, 'error')

        await component.saveMeasurement(mockMeasurement)

        expect(console.error).toHaveBeenCalledWith(jasmine.any(String), jasmine.any(Error))
    })
})