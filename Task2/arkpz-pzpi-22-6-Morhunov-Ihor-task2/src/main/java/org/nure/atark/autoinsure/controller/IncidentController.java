package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.IncidentDto;
import org.nure.atark.autoinsure.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Operation(summary = "Retrieve all incidents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of incidents retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<IncidentDto>> getAllIncidents() {
        List<IncidentDto> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @Operation(summary = "Retrieve an incident by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IncidentDto> getIncidentById(@PathVariable Integer id) {
        Optional<IncidentDto> incident = incidentService.getIncidentById(id);
        return incident.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new incident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident created successfully")
    })
    @PostMapping
    public ResponseEntity<IncidentDto> createIncident(@RequestBody IncidentDto incidentDto) {
        IncidentDto createdIncident = incidentService.createIncident(incidentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);
    }

    @Operation(summary = "Update an incident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident updated successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IncidentDto> updateIncident(@PathVariable Integer id, @RequestBody IncidentDto incidentDto) {
        Optional<IncidentDto> updatedIncident = incidentService.updateIncident(id, incidentDto);
        return updatedIncident.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete an incident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Incident deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Integer id) {
        return incidentService.deleteIncident(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
