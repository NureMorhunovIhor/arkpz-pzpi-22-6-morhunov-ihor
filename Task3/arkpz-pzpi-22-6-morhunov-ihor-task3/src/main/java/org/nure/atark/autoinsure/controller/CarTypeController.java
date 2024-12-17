package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.entity.CarType;
import org.nure.atark.autoinsure.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-logic/car-types")
public class CarTypeController {

    private final CarTypeService carTypeService;

    @Autowired
    public CarTypeController(CarTypeService carTypeService) {
        this.carTypeService = carTypeService;
    }

    @Operation(summary = "Retrieve all car types", description = "Fetch a list of all car types in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of car types retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarType.class)))
    })
    @GetMapping
    public List<CarType> getAllCarTypes() {
        return carTypeService.getAllCarTypes();
    }

    @Operation(summary = "Retrieve a car type by ID", description = "Fetch a single car type by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car type retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarType.class))),
            @ApiResponse(responseCode = "404", description = "Car type not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}"))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarTypeById(@PathVariable Integer id) {
        try {
            CarType carType = carTypeService.getCarTypeById(id);
            return ResponseEntity.ok(carType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Create a new car type", description = "Add a new car type to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car type created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarType.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}"))),
    })
    @PreAuthorize("hasAuthority('business_logic_admin')")
    @PostMapping
    public ResponseEntity<?> createCarType(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the car type to create",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"carTypeName\": \"string\",\n  \"minTirePressure\": 0,\n  \"maxTirePressure\": 0,\n  \"minFuelLevel\": 0,\n  \"maxFuelLevel\": 0,\n  \"minEngineTemp\": 0,\n  \"maxEngineTemp\": 0\n}"),
                            schema = @Schema(implementation = CarType.class)))
            @RequestBody CarType carType) {
        try {
            CarType createdCarType = carTypeService.createCarType(carType);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCarType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing car type", description = "Modify the details of an existing car type by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car type updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarType.class))),
            @ApiResponse(responseCode = "404", description = "Car type not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}"))),
    })
    @PreAuthorize("hasAuthority('business_logic_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarType(@PathVariable Integer id,
                                           @RequestBody CarType carTypeDetails) {
        try {
            CarType updatedCarType = carTypeService.updateCarType(id, carTypeDetails);
            return ResponseEntity.ok(updatedCarType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Delete a car type", description = "Remove a car type from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car type not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}"))),
    })
    @PreAuthorize("hasAuthority('business_logic_admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarType(@PathVariable Integer id) {
        try {
            carTypeService.deleteCarType(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
