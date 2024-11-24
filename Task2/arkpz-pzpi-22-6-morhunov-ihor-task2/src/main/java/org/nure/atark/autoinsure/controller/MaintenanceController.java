package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(summary = "Retrieve all maintenance records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of maintenance records retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<MaintenanceDto>> getAllMaintenance() {
        List<MaintenanceDto> maintenances = maintenanceService.getAllMaintenance();
        return ResponseEntity.ok(maintenances);
    }

    @Operation(summary = "Retrieve maintenance by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDto> getMaintenanceById(@PathVariable Integer id) {
        Optional<MaintenanceDto> maintenance = maintenanceService.getMaintenanceById(id);
        return maintenance.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new maintenance record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Maintenance record created successfully")
    })
    @PostMapping
    public ResponseEntity<MaintenanceDto> createMaintenance(@RequestBody MaintenanceDto maintenanceDto) {
        MaintenanceDto createdMaintenance = maintenanceService.createMaintenance(maintenanceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMaintenance);
    }

    @Operation(summary = "Update maintenance record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceDto> updateMaintenance(@PathVariable Integer id, @RequestBody MaintenanceDto maintenanceDto) {
        Optional<MaintenanceDto> updatedMaintenance = maintenanceService.updateMaintenance(id, maintenanceDto);
        return updatedMaintenance.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete maintenance record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Maintenance record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Integer id) {
        return maintenanceService.deleteMaintenance(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
