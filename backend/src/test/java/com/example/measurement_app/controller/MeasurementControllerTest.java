package com.example.measurement_app.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.measurement_app.model.Measurement;
import com.example.measurement_app.service.MeasurementService;

import jakarta.persistence.EntityNotFoundException;

class MeasurementControllerTest {

    private MeasurementService measurementService;
    private MeasurementController measurementController;

    @BeforeEach
    public void setUp() {
        // Mock the service and initialize the controller
        measurementService = mock(MeasurementService.class);
        measurementController = new MeasurementController(measurementService);
    }

    /**
     * Test saving a measurement.
     */
    @Test
    void shouldSaveMeasurementSuccessfully() {
        // Arrange: Create a valid measurement
        Measurement measurement = new Measurement();
        measurement.setId(1L);
        measurement.setPatientId(123L);
        measurement.setResult(75.0);
        measurement.setUuid(UUID.randomUUID());

        when(measurementService.saveMeasurement(any(Measurement.class))).thenReturn(measurement);

        // Act: Save the measurement
        ResponseEntity<Measurement> response = measurementController.saveMeasurement(measurement);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Measurement body = response.getBody();
        assertNotNull(body);
        assertEquals(123L, body.getPatientId());
        assertEquals(75.0, body.getResult());
        assertNotNull(body.getUuid());
        verify(measurementService, times(1)).saveMeasurement(measurement);
    }

    /**
     * Test retrieving a measurement by ID.
     */
    @Test
    void shouldRetrieveMeasurementByIdSuccessfully() {
        // Arrange: Create a valid measurement
        Measurement measurement = new Measurement();
        measurement.setId(1L);
        measurement.setPatientId(123L);
        measurement.setResult(75.0);
        measurement.setUuid(UUID.randomUUID());

        when(measurementService.getMeasurementById(1L)).thenReturn(Optional.of(measurement));

        // Act: Retrieve the measurement by ID
        ResponseEntity<Measurement> response = measurementController.getMeasurementById(1L);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Measurement body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
        assertEquals(123L, body.getPatientId());
        assertEquals(75.0, body.getResult());
        assertNotNull(body.getUuid());
        verify(measurementService, times(1)).getMeasurementById(1L);
    }

    /**
     * Test retrieving a measurement by a non-existent ID.
     */
    @Test
    void shouldReturnNotFoundForNonExistentMeasurementById() {
        // Arrange: Simulate no measurement found
        when(measurementService.getMeasurementById(1L)).thenReturn(Optional.empty());

        // Act: Attempt to retrieve the measurement
        ResponseEntity<Measurement> response = measurementController.getMeasurementById(1L);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(measurementService, times(1)).getMeasurementById(1L);
    }

    /**
     * Test retrieving a measurement by UUID.
     */
    @Test
    void shouldRetrieveMeasurementByUuidSuccessfully() {
        // Arrange: Create a valid measurement
        UUID uuid = UUID.randomUUID();
        Measurement measurement = new Measurement();
        measurement.setId(1L);
        measurement.setUuid(uuid);
        measurement.setPatientId(456L);
        measurement.setResult(88.5);

        when(measurementService.getMeasurementByUuid(uuid)).thenReturn(Optional.of(measurement));

        // Act: Retrieve the measurement by UUID
        ResponseEntity<Measurement> response = measurementController.getMeasurementByUuid(uuid);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Measurement body = response.getBody();
        assertNotNull(body);
        assertEquals(uuid, body.getUuid());
        assertEquals(456L, body.getPatientId());
        assertEquals(88.5, body.getResult());
        verify(measurementService, times(1)).getMeasurementByUuid(uuid);
    }

    /**
     * Test retrieving all measurements.
     */
    @Test
    void shouldRetrieveAllMeasurementsSuccessfully() {
        // Arrange: Create a list of measurements
        Measurement m1 = new Measurement();
        m1.setId(1L);
        m1.setPatientId(100L);
        m1.setResult(65.0);
        m1.setUuid(UUID.randomUUID());

        Measurement m2 = new Measurement();
        m2.setId(2L);
        m2.setPatientId(200L);
        m2.setResult(85.0);
        m2.setUuid(UUID.randomUUID());

        when(measurementService.getAllMeasurements()).thenReturn(List.of(m1, m2));

        // Act: Retrieve all measurements
        ResponseEntity<List<Measurement>> response = measurementController.getAllMeasurements();

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Measurement> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals(100L, body.get(0).getPatientId());
        assertEquals(200L, body.get(1).getPatientId());

        verify(measurementService, times(1)).getAllMeasurements();
    }

    /**
     * Test deleting a measurement by ID.
     */
    @Test
    void shouldDeleteMeasurementByUUIDSuccessfully() {
        // Arrange: Simulate successful deletion
        doNothing().when(measurementService).deleteMeasurementByUuid(any(UUID.class));

        // Act: Delete the measurement
        ResponseEntity<Void> response = measurementController.deleteMeasurementByUuid(UUID.randomUUID());
        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(measurementService, times(1)).deleteMeasurementByUuid(any(UUID.class));
    }

    /**
     * Test updating a measurement by UUID with a conflict (duplicate key
     * error).
     */
    @Test
    void shouldReturnConflictWhenDuplicateKeyErrorOccursDuringUpdate() {
        UUID uuid = UUID.randomUUID();
        Measurement updatedMeasurement = new Measurement();
        updatedMeasurement.setPatientId(1L);
        updatedMeasurement.setResult(80.0);

        // Mock the service method that the controller actually calls
        when(measurementService.updateMeasurementByUuid(uuid, updatedMeasurement))
                .thenThrow(new DataIntegrityViolationException("Duplicate key error"));

        ResponseEntity<Measurement> response = measurementController.updateMeasurementByUuid(uuid, updatedMeasurement);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());

        verify(measurementService, times(1)).updateMeasurementByUuid(uuid, updatedMeasurement);
    }

    /**
     * Test updating a measurement by UUID - measurement not found (404).
     */
    @Test
    void shouldReturnNotFoundWhenMeasurementDoesNotExistDuringUpdate() {
        UUID uuid = UUID.randomUUID();
        Measurement updatedMeasurement = new Measurement();
        updatedMeasurement.setPatientId(456L);
        updatedMeasurement.setResult(99.9);

        // Mock service to throw EntityNotFoundException
        when(measurementService.updateMeasurementByUuid(uuid, updatedMeasurement))
                .thenThrow(new EntityNotFoundException("Measurement with UUID " + uuid + " not found"));

        ResponseEntity<Measurement> response = measurementController.updateMeasurementByUuid(uuid, updatedMeasurement);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(measurementService, times(1)).updateMeasurementByUuid(uuid, updatedMeasurement);
    }

    /**
     * Test updating a measurement by UUID - conflict error (409).
     */
    @Test
    void testUpdateMeasurementByUuid() {
        UUID uuid = UUID.randomUUID();
        Measurement updatedMeasurement = new Measurement();
        updatedMeasurement.setPatientId(1L);
        updatedMeasurement.setResult(80.0);

        when(measurementService.updateMeasurementByUuid(uuid, updatedMeasurement))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        ResponseEntity<Measurement> response = measurementController.updateMeasurementByUuid(uuid, updatedMeasurement);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());

        verify(measurementService, times(1)).updateMeasurementByUuid(uuid, updatedMeasurement);
    }

}
