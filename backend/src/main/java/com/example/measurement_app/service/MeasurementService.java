package com.example.measurement_app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.measurement_app.model.Measurement;
import com.example.measurement_app.repository.MeasurementRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public Measurement saveMeasurement(Measurement measurement) {
        return measurementRepository.save(measurement);
    }

    public Optional<Measurement> getMeasurementById(Long id) {
        return measurementRepository.findById(id);
    }

    public Optional<Measurement> getMeasurementByUuid(UUID uuid) {
        return measurementRepository.findByUuid(uuid);
    }

    public List<Measurement> getAllMeasurements() {
        return measurementRepository.findAll();
    }

    @Transactional
    public void deleteMeasurementByUuid(UUID uuid) {
        Optional<Measurement> measurement = measurementRepository.findByUuid(uuid);
        if (measurement.isPresent()) {
            measurementRepository.deleteByUuid(uuid);
        } else {
            throw new IllegalArgumentException("Measurement with UUID " + uuid + " not found.");
        }
    }

    public Measurement updateMeasurementByUuid(UUID uuid, Measurement updatedMeasurement) {
        Optional<Measurement> existing = measurementRepository.findByUuid(uuid);
        if (existing.isPresent()) {
            Measurement existingMeasurement = existing.get();
            existingMeasurement.setPatientId(updatedMeasurement.getPatientId());
            existingMeasurement.setResult(updatedMeasurement.getResult());
            return measurementRepository.save(existingMeasurement);
        } else {
            throw new EntityNotFoundException("Measurement with UUID " + uuid + " not found");
        }
    }

}
