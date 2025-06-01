package com.example.measurement_app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.measurement_app.model.Measurement;
import com.example.measurement_app.repository.MeasurementRepository;

class MeasurementServiceTest {

    private MeasurementRepository measurementRepository;
    private MeasurementService measurementService;

    @BeforeEach
    public void setUp() {
        measurementRepository = mock(MeasurementRepository.class);
        measurementService = new MeasurementService(measurementRepository);
    }

    /**
     * Test saving a measurement.
     */
    @Test
    void shouldSaveMeasurementSuccessfully() {
        // Arrange: Create a valid measurement
        Measurement measurement = new Measurement();
        measurement.setPatientId(123L);
        measurement.setResult(75.0);

        when(measurementRepository.save(measurement)).thenReturn(measurement);

        // Act: Save the measurement
        Measurement savedMeasurement = measurementService.saveMeasurement(measurement);

        // Assert: Verify the saved measurement
        assertNotNull(savedMeasurement);
        assertEquals(123L, savedMeasurement.getPatientId());
        verify(measurementRepository, times(1)).save(measurement);
    }

    /**
     * Test retrieving a measurement by ID.
     */
    @Test
    void shouldRetrieveMeasurementByIdSuccessfully() {
        // Arrange: Create a valid measurement
        Measurement measurement = new Measurement();
        measurement.setId(1L);

        when(measurementRepository.findById(1L)).thenReturn(Optional.of(measurement));

        // Act: Retrieve the measurement by ID
        Optional<Measurement> foundMeasurement = measurementService.getMeasurementById(1L);

        // Assert: Verify the retrieved measurement
        assertTrue(foundMeasurement.isPresent());
        assertEquals(1L, foundMeasurement.get().getId());
        verify(measurementRepository, times(1)).findById(1L);
    }

    /**
     * Test retrieving all measurements.
     */
    @Test
    void shouldRetrieveAllMeasurementsSuccessfully() {
        // Arrange: Create a list of measurements
        Measurement m1 = new Measurement();
        Measurement m2 = new Measurement();

        when(measurementRepository.findAll()).thenReturn(List.of(m1, m2));

        // Act: Retrieve all measurements
        List<Measurement> measurements = measurementService.getAllMeasurements();

        // Assert: Verify the retrieved measurements
        assertEquals(2, measurements.size());
        verify(measurementRepository, times(1)).findAll();
    }

    /**
     * Test deleting a measurement by UUID.
     */
    @Test
    void shouldDeleteMeasurementByUuidSuccessfully() {
        // Arrange: Mock the repository behavior
        UUID uuid = UUID.randomUUID();
        Measurement measurement = new Measurement();
        measurement.setUuid(uuid);

        when(measurementRepository.findByUuid(uuid)).thenReturn(Optional.of(measurement));
        doNothing().when(measurementRepository).deleteByUuid(uuid);

        // Act: Call the service method
        measurementService.deleteMeasurementByUuid(uuid);

        // Assert: Verify interactions with the repository
        verify(measurementRepository, times(1)).findByUuid(uuid);
        verify(measurementRepository, times(1)).deleteByUuid(uuid);
    }

    /**
     * Test updating a measurement by UUID.
     */
    @Test
    void shouldUpdateMeasurementByUuidSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Measurement existingMeasurement = new Measurement();
        existingMeasurement.setUuid(uuid);
        existingMeasurement.setPatientId(1L);
        existingMeasurement.setResult(75.0);

        Measurement updatedMeasurement = new Measurement();
        updatedMeasurement.setPatientId(1L);
        updatedMeasurement.setResult(80.0);

        when(measurementRepository.findByUuid(uuid)).thenReturn(Optional.of(existingMeasurement));
        when(measurementRepository.save(existingMeasurement)).thenReturn(existingMeasurement);

        Measurement result = measurementService.updateMeasurementByUuid(uuid, updatedMeasurement);

        assertNotNull(result);
        assertEquals(80.0, result.getResult());
        verify(measurementRepository, times(1)).findByUuid(uuid);
        verify(measurementRepository, times(1)).save(existingMeasurement);
    }

}
