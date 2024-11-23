package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Operation(summary = "Retrieve all sensors", description = "Fetch a list of all sensors in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<SensorDto>> getAllSensors() {
        List<SensorDto> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @Operation(summary = "Retrieve a sensor by ID", description = "Fetch a single sensor by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SensorDto> getSensorById(@PathVariable Integer id) {
        return sensorService.getSensorById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new sensor", description = "Add a new sensor to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sensor created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<SensorDto> createSensor(@RequestBody SensorDto sensorDto) {
        SensorDto createdSensor = sensorService.createSensor(sensorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
    }

    @Operation(summary = "Update an existing sensor", description = "Modify the details of an existing sensor by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SensorDto> updateSensor(@PathVariable Integer id, @RequestBody SensorDto sensorDto) {
        return sensorService.updateSensor(id, sensorDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a sensor", description = "Remove a sensor from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sensor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Integer id) {
        if (sensorService.deleteSensor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
