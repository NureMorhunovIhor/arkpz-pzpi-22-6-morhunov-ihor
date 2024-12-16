package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @Autowired
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(summary = "Retrieve all maintenance records", description = "Fetch a list of all maintenance records in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of maintenance records retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaintenanceDto.class)))
    })
    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping
    public ResponseEntity<List<MaintenanceDto>> getAllMaintenance() {
        List<MaintenanceDto> maintenances = maintenanceService.getAllMaintenance();
        return ResponseEntity.ok(maintenances);
    }

    @Operation(summary = "Retrieve a maintenance record by ID", description = "Fetch a single maintenance record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance record retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaintenanceDto.class))),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMaintenanceById(@PathVariable Integer id) {
        try {
            Optional<MaintenanceDto> maintenance = maintenanceService.getMaintenanceById(id);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Retrieve all maintenance records for a specific car", description = "Fetch all maintenance records associated with a specific car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of maintenance records retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaintenanceDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getMaintenanceByCarId(@PathVariable Integer carId) {
        try {
            List<MaintenanceDto> maintenances = maintenanceService.getMaintenanceByCarId(carId);
            if (maintenances.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"No maintenance records found for this car\"}");
            }
            return ResponseEntity.ok(maintenances);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    @Operation(summary = "Create a new maintenance record", description = "Add a new maintenance record to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Maintenance record created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaintenanceDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PostMapping
    public ResponseEntity<?> createMaintenance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the maintenance record to create",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"maintenanceDate\": \"string\",\n  \"maintenanceType\": \"string\",\n  \"description\": \"string\",\n  \"cost\": 0,\n  \"carId\": 0\n}"),
                            schema = @Schema(implementation = MaintenanceDto.class)))
            @RequestBody MaintenanceDto maintenanceDto) {
        try {
            Optional<MaintenanceDto> createdMaintenance = Optional.ofNullable(maintenanceService.createMaintenance(maintenanceDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMaintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing maintenance record", description = "Modify the details of an existing maintenance record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance record updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaintenanceDto.class))),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Integer id,
                                               @RequestBody MaintenanceDto maintenanceDto) {
        try {
            Optional<MaintenanceDto> updatedMaintenance = maintenanceService.updateMaintenance(id, maintenanceDto);
            return ResponseEntity.ok(updatedMaintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Delete a maintenance record", description = "Remove a maintenance record from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Maintenance record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Integer id) {
        try {
            maintenanceService.deleteMaintenance(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
