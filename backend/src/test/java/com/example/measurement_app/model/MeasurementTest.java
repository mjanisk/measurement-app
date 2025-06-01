package com.example.measurement_app.model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class MeasurementTest {

    /**
     * Test the default constructor of the Measurement class.
     */
    @Test
    void shouldInitializeMeasurementWithDefaultValues() {
        // Act: Create a new Measurement instance
        Measurement measurement = new Measurement();

        // Assert: Verify the default values
        assertNotNull(measurement.getUuid(), "UUID should not be null");
        assertNull(measurement.getId(), "ID should be null by default");
        assertNull(measurement.getPatientId(), "Patient ID should be null by default");
        assertNull(measurement.getResult(), "Result should be null by default");
    }

    /**
     * Test setting and getting the ID of a Measurement.
     */
    @Test
    void shouldSetAndGetIdSuccessfully() {
        // Arrange: Create a Measurement instance and a sample ID
        Measurement measurement = new Measurement();
        Long id = 1L;

        // Act: Set the ID
        measurement.setId(id);

        // Assert: Verify the ID was set correctly
        assertEquals(id, measurement.getId(), "ID should match the value set");
    }

    /**
     * Test setting and getting the Patient ID of a Measurement.
     */
    @Test
    void shouldSetAndGetPatientIdSuccessfully() {
        // Arrange: Create a Measurement instance and a sample Patient ID
        Measurement measurement = new Measurement();
        Long patientId = 123L;

        // Act: Set the Patient ID
        measurement.setPatientId(patientId);

        // Assert: Verify the Patient ID was set correctly
        assertEquals(patientId, measurement.getPatientId(), "Patient ID should match the value set");
    }

    /**
     * Test setting and getting the Result of a Measurement.
     */
    @Test
    void shouldSetAndGetResultSuccessfully() {
        // Arrange: Create a Measurement instance and a sample Result
        Measurement measurement = new Measurement();
        Double result = 75.5;

        // Act: Set the Result
        measurement.setResult(result);

        // Assert: Verify the Result was set correctly
        assertEquals(result, measurement.getResult(), "Result should match the value set");
    }

    /**
     * Test setting and getting the UUID of a Measurement.
     */
    @Test
    void shouldSetAndGetUuidSuccessfully() {
        // Arrange: Create a Measurement instance and a sample UUID
        Measurement measurement = new Measurement();
        UUID uuid = UUID.randomUUID();

        // Act: Set the UUID
        measurement.setUuid(uuid);

        // Assert: Verify the UUID was set correctly
        assertEquals(uuid, measurement.getUuid(), "UUID should match the value set");
    }
}