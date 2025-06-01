package com.example.measurement_app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.measurement_app.model.Measurement;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    // Find a measurement by its UUID
    Optional<Measurement> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}