package com.example.measurement_app.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore 
    @Schema(hidden = true) 
    private Long id;

    @NotNull(message = "Patient ID is required.")
    @Positive(message = "Patient ID must be a positive number.")
    @Column(name = "patient_id", nullable = false)
    @Schema(description = "ID of the patient associated with the measurement", example = "123")
    private Long patientId;

    @NotNull(message = "Result is required.")
    @DecimalMin(value = "50.0", message = "Result must be at least 50.0")
    @DecimalMax(value = "100.0", message = "Result must not exceed 100.0")
    @Column(name = "result", nullable = false)
    @Schema(description = "Result of the measurement (must be between 50 and 100)", example = "75.5")
    private Double result;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    @JsonProperty(access = Access.READ_ONLY) 
    @Schema(hidden = true) 
    private UUID uuid = UUID.randomUUID();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}