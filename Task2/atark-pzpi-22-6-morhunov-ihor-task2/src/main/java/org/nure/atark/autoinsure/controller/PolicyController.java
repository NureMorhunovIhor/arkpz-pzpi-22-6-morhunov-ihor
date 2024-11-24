package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Operation(summary = "Retrieve all policy records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of policies retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        List<PolicyDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @Operation(summary = "Retrieve policy by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Policy record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDto> getPolicyById(@PathVariable Integer id) {
        Optional<PolicyDto> policy = policyService.getPolicyById(id);
        return policy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new policy record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Policy record created successfully")
    })
    @PostMapping
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody PolicyDto policyDto) {
        PolicyDto createdPolicy = policyService.createPolicy(policyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    @Operation(summary = "Update an existing policy record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Policy record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PolicyDto> updatePolicy(@PathVariable Integer id, @RequestBody PolicyDto policyDto) {
        Optional<PolicyDto> updatedPolicy = policyService.updatePolicy(id, policyDto);
        return updatedPolicy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete policy record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Policy record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Policy record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Integer id) {
        return policyService.deletePolicy(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
