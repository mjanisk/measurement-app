package com.example.measurement_app.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.measurement_app.model.Measurement;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles("test")

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeasurementRepositoryTest {

    @Autowired
    private MeasurementRepository measurementRepository;

    /**
     * Test saving a valid measurement and retrieving it by ID.
     */
    @Test
    void shouldSaveAndRetrieveMeasurementById() {
        // Arrange: Create a valid measurement
        Measurement measurement = new Measurement();
        measurement.setPatientId(123L);
        measurement.setResult(75.0);

        // Act: Save the measurement and retrieve it by ID
        Measurement savedMeasurement = measurementRepository.save(measurement);
        Optional<Measurement> foundMeasurement = measurementRepository.findById(savedMeasurement.getId());

        // Assert: Verify the measurement was saved and retrieved successfully
        assertTrue(foundMeasurement.isPresent());
        assertEquals(savedMeasurement.getId(), foundMeasurement.get().getId());
    }

    /**
     * Test finding a measurement by its UUID.
     */
    @Test
    void shouldFindMeasurementByUuid() {
        // Arrange: Create and save a valid measurement
        Measurement measurement = new Measurement();
        measurement.setPatientId(456L);
        measurement.setResult(85.0);
        Measurement savedMeasurement = measurementRepository.save(measurement);

        // Act: Retrieve the measurement by UUID
        Optional<Measurement> foundMeasurement = measurementRepository.findByUuid(savedMeasurement.getUuid());

        // Assert: Verify the measurement was retrieved successfully
        assertTrue(foundMeasurement.isPresent());
        assertEquals(savedMeasurement.getUuid(), foundMeasurement.get().getUuid());
    }

  

    /**
     * Test saving an invalid measurement and expecting a validation exception.
     */
    @Test
    void shouldThrowValidationExceptionForInvalidMeasurement() {
        // Arrange: Create an invalid measurement
        Measurement invalidMeasurement = new Measurement();
        invalidMeasurement.setPatientId(-1L); // Invalid patient ID
        invalidMeasurement.setResult(120.0);  // Invalid result

        // Act & Assert: Verify that saving the invalid measurement throws a ConstraintViolationException
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> measurementRepository.saveAndFlush(invalidMeasurement)
        );

        // Assert: Verify the exception contains validation error messages
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("patientId") || exception.getMessage().contains("result"));
    }

    /**
     * Test retrieving a measurement by a non-existent ID.
     */
    @Test
    void shouldReturnEmptyOptionalForNonExistentMeasurement() {
        // Act: Attempt to retrieve a measurement by a non-existent ID
        Optional<Measurement> foundMeasurement = measurementRepository.findById(999L);

        // Assert: Verify that no measurement is found
        assertFalse(foundMeasurement.isPresent());
    }
}