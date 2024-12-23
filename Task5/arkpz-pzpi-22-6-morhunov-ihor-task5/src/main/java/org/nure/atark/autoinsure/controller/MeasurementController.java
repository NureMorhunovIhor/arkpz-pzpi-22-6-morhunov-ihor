package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.MeasurementDto;
import org.nure.atark.autoinsure.service.MeasurementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping
    public ResponseEntity<List<MeasurementDto>> getAllMeasurements() {
        List<MeasurementDto> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeasurementById(@PathVariable Integer id) {
        try {
            MeasurementDto measurement = measurementService.getMeasurementById(id);
            return ResponseEntity.ok(measurement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> createMeasurement(@RequestBody MeasurementDto measurementDto) {
        try {
            MeasurementDto createdMeasurement = measurementService.createMeasurement(measurementDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeasurement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PreAuthorize("hasAuthority('administrator')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeasurement(@PathVariable Integer id) {
        try {
            measurementService.deleteMeasurement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
