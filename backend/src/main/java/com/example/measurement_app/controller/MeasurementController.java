package com.example.measurement_app.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.measurement_app.model.Measurement;
import com.example.measurement_app.service.MeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/measurements")
@Validated
@CrossOrigin(origins = "*")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @Operation(summary = "Save a new measurement", description = "Creates a new measurement and saves it to the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement saved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Measurement> saveMeasurement(
            @Valid @RequestBody @Parameter(description = "Measurement object to be saved") Measurement measurement) {
        Measurement savedMeasurement = measurementService.saveMeasurement(measurement);
        return ResponseEntity.ok(savedMeasurement);
    }

    @Operation(summary = "Get a measurement by ID", description = "Retrieves a measurement by its unique ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class))),
        @ApiResponse(responseCode = "404", description = "Measurement not found",
                content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Measurement> getMeasurementById(
            @PathVariable @Parameter(description = "ID of the measurement to retrieve") Long id) {
        Optional<Measurement> measurement = measurementService.getMeasurementById(id);
        return measurement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a measurement by UUID", description = "Retrieves a measurement by its unique UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class))),
        @ApiResponse(responseCode = "404", description = "Measurement not found",
                content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<Measurement> getMeasurementByUuid(
            @PathVariable @Parameter(description = "UUID of the measurement to retrieve") UUID uuid) {
        Optional<Measurement> measurement = measurementService.getMeasurementByUuid(uuid);
        return measurement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all measurements", description = "Retrieves all measurements from the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class)))
    })
    @GetMapping
    public ResponseEntity<List<Measurement>> getAllMeasurements() {
        List<Measurement> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }

    @Operation(summary = "Delete a measurement by UUID", description = "Deletes a measurement by its unique UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Measurement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found")
    })
    @DeleteMapping("/uuid/{uuid}")
    public ResponseEntity<Void> deleteMeasurementByUuid(
            @PathVariable @Parameter(description = "UUID of the measurement to delete") UUID uuid) {
        measurementService.deleteMeasurementByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a measurement by UUID", description = "Updates an existing measurement by its unique UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class))),
        @ApiResponse(responseCode = "404", description = "Measurement not found",
                content = @Content(mediaType = "application/json"))
    })
    
    @PutMapping("/uuid/{uuid}")
    public ResponseEntity<Measurement> updateMeasurementByUuid(
            @PathVariable UUID uuid,
            @Valid @RequestBody Measurement updatedMeasurement) {
        try {
            Measurement savedMeasurement = measurementService.updateMeasurementByUuid(uuid, updatedMeasurement);
            return ResponseEntity.ok(savedMeasurement);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

}
