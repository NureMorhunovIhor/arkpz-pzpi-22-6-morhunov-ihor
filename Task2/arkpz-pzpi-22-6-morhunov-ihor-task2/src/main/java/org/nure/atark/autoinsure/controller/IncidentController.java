package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.IncidentDto;
import org.nure.atark.autoinsure.service.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Retrieve all incidents", description = "Fetch a list of all incidents in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of incidents retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class)))
    })
    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping
    public ResponseEntity<List<IncidentDto>> getAllIncidents() {
        List<IncidentDto> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @Operation(summary = "Retrieve an incident by ID", description = "Fetch a single incident by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getIncidentById(@PathVariable Integer id) {
        try {
            Optional<IncidentDto> incident = incidentService.getIncidentById(id);
            return ResponseEntity.ok(incident);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Retrieve incidents for a user", description = "Fetch incidents related to a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidents retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getIncidentsByUserId(@PathVariable Integer userId) {
        try {
            List<IncidentDto> incidents = incidentService.getIncidentsByUserId(userId);
            if(incidents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"User has no incidents\"}");
            }
            return ResponseEntity.ok(incidents);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Retrieve incidents for a car", description = "Fetch incidents related to a specific car.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidents retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getIncidentsByCarId(@PathVariable Integer carId) {
        try {
            List<IncidentDto> incidents = incidentService.getIncidentsByCarId(carId);
            if (incidents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Car has no incidents\"}");
            }
            return ResponseEntity.ok(incidents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    @Operation(summary = "Create a new incident", description = "Add a new incident to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PostMapping
    public ResponseEntity<?> createIncident(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the incident to create",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"incidentDate\": \"2024-11-27\",\n  \"incidentType\": \"string\",\n  \"description\": \"string\",\n  \"carId\": 0,\n  \"sensorId\": 0}"),
                            schema = @Schema(implementation = IncidentDto.class)))
            @RequestBody IncidentDto incidentDto) {
        try {
            Optional<IncidentDto> createdIncident = Optional.ofNullable(incidentService.createIncident(incidentDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing incident", description = "Modify the details of an existing incident by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncidentDto.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncident(@PathVariable Integer id,
                                            @RequestBody IncidentDto incidentDto) {
        try {
            Optional<IncidentDto> updatedIncident = incidentService.updateIncident(id, incidentDto);
            return ResponseEntity.ok(updatedIncident);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Delete an incident", description = "Remove an incident from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Incident deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })

    @PreAuthorize("hasAuthority('administrator')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncident(@PathVariable Integer id) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
