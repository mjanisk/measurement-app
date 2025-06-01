import { TestBed } from '@angular/core/testing'
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing'
import { MeasurementService, Measurement } from './measurement.service'
import { MatSnackBarModule } from '@angular/material/snack-bar'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'

describe('MeasurementService', () => {
    let service: MeasurementService
    let httpMock: HttpTestingController

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
                MatSnackBarModule,
                BrowserAnimationsModule,
            ],
            providers: [MeasurementService],
        })
        service = TestBed.inject(MeasurementService)
        httpMock = TestBed.inject(HttpTestingController)
    })

    afterEach(() => {
        httpMock.verify()
    })

    it('should be created', () => {
        expect(service).toBeTruthy()
    })

    it('should fetch all measurements', () => {
        const mockMeasurements: Measurement[] = [
            { uuid: '1', patientId: 101, result: 98.6 },
            { uuid: '2', patientId: 102, result: 99.1 },
        ]

        service.getAllMeasurements().subscribe((measurements) => {
            expect(measurements.length).toBe(2)
            expect(measurements).toEqual(mockMeasurements)
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements')
        expect(req.request.method).toBe('GET')
        req.flush(mockMeasurements)
    })

    it('should save a measurement', () => {
        const newMeasurement: Measurement = { uuid: '3', patientId: 103, result: 97.5 }

        service.saveMeasurement(newMeasurement).subscribe((measurement) => {
            expect(measurement).toEqual(newMeasurement)
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements')
        expect(req.request.method).toBe('POST')
        req.flush(newMeasurement)
    })

    it('should fetch a measurement by UUID', () => {
        const mockMeasurement: Measurement = { uuid: '1', patientId: 101, result: 98.6 }

        service.getMeasurementByUuid('1').subscribe((measurement) => {
            expect(measurement).toEqual(mockMeasurement)
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements/uuid/1')
        expect(req.request.method).toBe('GET')
        req.flush(mockMeasurement)
    })

    it('should delete a measurement by UUID', () => {
        service.deleteMeasurementByUuid('1').subscribe((response) => {
            expect(response).toBeNull()
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements/uuid/1')
        expect(req.request.method).toBe('DELETE')
        req.flush(null)
    })

    it('should update a measurement', () => {
        const updatedMeasurement: Measurement = { uuid: '1', patientId: 101, result: 99.0 }

        service.updateMeasurement(updatedMeasurement).subscribe((measurement) => {
            expect(measurement).toEqual(updatedMeasurement)
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements/uuid/1')
        expect(req.request.method).toBe('PUT')
        req.flush(updatedMeasurement)
    })

    it('should add a measurement', () => {
        const newMeasurement: Measurement = { uuid: '4', patientId: 104, result: 96.8 }

        service.addMeasurement(newMeasurement).subscribe((measurement) => {
            expect(measurement).toEqual(newMeasurement)
        })

        const req = httpMock.expectOne('http://localhost:8080/measurements')
        expect(req.request.method).toBe('POST')
        req.flush(newMeasurement)
    })

    it('should handle service response with success message', () => {
        const mockMeasurement: Measurement = { uuid: '5', patientId: 105, result: 97.0 }

        service
            .handleServiceResponse(
                service.addMeasurement(mockMeasurement),
                'Measurement added successfully',
                (response) => {
                    expect(response).toEqual(mockMeasurement)
                }
            )
            .subscribe()

        const req = httpMock.expectOne('http://localhost:8080/measurements')
        expect(req.request.method).toBe('POST')
        req.flush(mockMeasurement)
    })

    it('should fetch all measurements asynchronously', async () => {
        const mockMeasurements: Measurement[] = [
            { uuid: '1', patientId: 101, result: 98.6 },
            { uuid: '2', patientId: 102, result: 99.1 },
        ]

        const fetchPromise = service.fetchAllMeasurements() // Start the async operation

        const req = httpMock.expectOne('http://localhost:8080/measurements')
        expect(req.request.method).toBe('GET')
        req.flush(mockMeasurements) // Respond to the request

        const measurements = await fetchPromise // Await the result
        expect(measurements).toEqual(mockMeasurements)
    })

    it('should delete a measurement asynchronously', async () => {
        const deletePromise = service.deleteMeasurement('1') // Start the async operation

        const req = httpMock.expectOne('http://localhost:8080/measurements/uuid/1')
        expect(req.request.method).toBe('DELETE')
        req.flush(null) // Respond to the request

        await deletePromise // Await the result
    })
})
