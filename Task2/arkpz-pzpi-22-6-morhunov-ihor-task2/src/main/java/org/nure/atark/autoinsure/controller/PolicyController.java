package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    @Autowired
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Operation(summary = "Retrieve all policies", description = "Fetch a list of all policies in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of policies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        List<PolicyDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @Operation(summary = "Retrieve a policy by ID", description = "Fetch a single policy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyDto.class))),
            @ApiResponse(responseCode = "404", description = "Policy not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPolicyById(@PathVariable Integer id) {
        try {
            Optional<PolicyDto> policy = policyService.getPolicyById(id);
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Create a new policy", description = "Add a new policy to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Policy created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PostMapping
    public ResponseEntity<?> createPolicy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the policy to create",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"policyNumber\": \"string\",\n  \"startDate\": \"string\",\n  \"endDate\": \"string\",\n  \"price\": 0,\n  \"carId\": 0\n}"),
                            schema = @Schema(implementation = PolicyDto.class)))
            @RequestBody PolicyDto policyDto) {
        try {
            Optional<PolicyDto> createdPolicy = Optional.ofNullable(policyService.createPolicy(policyDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing policy", description = "Modify the details of an existing policy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyDto.class))),
            @ApiResponse(responseCode = "404", description = "Policy not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePolicy(@PathVariable Integer id,
                                          @RequestBody PolicyDto policyDto) {
        try {
            Optional<PolicyDto> updatedPolicy = policyService.updatePolicy(id, policyDto);
            return ResponseEntity.ok(updatedPolicy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Delete a policy", description = "Remove a policy from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Policy deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePolicy(@PathVariable Integer id) {
        try {
            policyService.deletePolicy(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
