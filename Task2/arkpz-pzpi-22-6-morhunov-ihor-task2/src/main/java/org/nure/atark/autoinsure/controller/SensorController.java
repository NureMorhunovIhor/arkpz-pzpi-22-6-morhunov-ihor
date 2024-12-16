package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Retrieve all sensor records", description = "Fetch a list of all sensors in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sensors retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SensorDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<SensorDto>> getAllSensors() {
        List<SensorDto> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @Operation(summary = "Retrieve sensor by ID", description = "Fetch a single sensor by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor record retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "404", description = "Sensor record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSensorById(@PathVariable Integer id) {
        try {
            Optional<SensorDto> sensor = sensorService.getSensorById(id);
            return ResponseEntity.ok(sensor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    @Operation(summary = "Retrieve all sensors for a specific car", description = "Fetch a list of all sensors associated with a specific car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sensors retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getSensorsByCarId(@PathVariable Integer carId) {
        try {
            List<SensorDto> sensors = sensorService.getSensorsByCarId(carId);
            if (sensors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"No sensors found for car with ID: " + carId + "\"}");
            }
            return ResponseEntity.ok(sensors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Create a new sensor record", description = "Add a new sensor to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sensor record created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PostMapping
    public ResponseEntity<?> createSensor(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Details of the policy to create",
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\n  \"sensorType\": \"string\",\n  \"currentState\": \"string\",\n  \"lastUpdate\": \"2024-11-27\",\n  \"carId\": 0\n}"),
                    schema = @Schema(implementation = PolicyDto.class)))
            @RequestBody SensorDto sensorDto) {
        try {
            Optional<SensorDto> createdSensor = Optional.ofNullable(sensorService.createSensor(sensorDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing sensor record", description = "Modify the details of an existing sensor by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor record updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SensorDto.class))),
            @ApiResponse(responseCode = "404", description = "Sensor record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSensor(@PathVariable Integer id, @RequestBody SensorDto sensorDto) {
        try {
            Optional<SensorDto> updatedSensor = sensorService.updateSensor(id, sensorDto);
            return ResponseEntity.ok(updatedSensor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Delete sensor record", description = "Remove a sensor from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sensor record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sensor record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSensor(@PathVariable Integer id) {
        try {
            sensorService.deleteSensor(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
