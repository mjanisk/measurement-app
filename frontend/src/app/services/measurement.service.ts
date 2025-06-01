import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable, firstValueFrom } from 'rxjs'
import { catchError, tap } from 'rxjs/operators'
import { MatSnackBar } from '@angular/material/snack-bar'

export interface Measurement {
    uuid?: string
    patientId: number
    result: number
}

@Injectable({
    providedIn: 'root',
})
export class MeasurementService {
    private baseUrl = 'http://localhost:8080/measurements';

    constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

    getAllMeasurements(): Observable<Measurement[]> {
        return this.http.get<Measurement[]>(`${this.baseUrl}`)
    }

    saveMeasurement(measurement: Measurement): Observable<Measurement> {
        return this.http.post<Measurement>(`${this.baseUrl}`, measurement)
    }

    getMeasurementByUuid(uuid: string): Observable<Measurement> {
        return this.http.get<Measurement>(`${this.baseUrl}/uuid/${uuid}`)
    }

    deleteMeasurementByUuid(uuid: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/uuid/${uuid}`)
    }

    updateMeasurement(measurement: Measurement): Observable<Measurement> {
        return this.http.put<Measurement>(`${this.baseUrl}/uuid/${measurement.uuid}`, measurement)
    }

    addMeasurement(measurement: Measurement): Observable<Measurement> {
        return this.http.post<Measurement>(`${this.baseUrl}`, measurement)
    }

    async fetchAllMeasurements(): Promise<Measurement[]> {
        return firstValueFrom(this.getAllMeasurements())
    }

    async saveMeasurementAsync(measurement: Measurement): Promise<Measurement> {
        return firstValueFrom(this.updateMeasurement(measurement))
    }

    async deleteMeasurement(uuid: string): Promise<void> {
        return firstValueFrom(this.deleteMeasurementByUuid(uuid))
    }

    handleServiceResponse<T>(observable: Observable<T>, successMessage: string, onSuccess?: (response: T) => void): Observable<T> {
        return observable.pipe(
            tap(response => {
                if (onSuccess) {
                    onSuccess(response)
                }
                this.snackBar.open(successMessage, 'Close', { duration: 3000 })
            }),
            catchError(error => {
                this.snackBar.open('An unexpected error occurred.', 'Close', { duration: 3000 })
                console.error('Service Error:', error)
                throw error
            })
        )
    }
}
