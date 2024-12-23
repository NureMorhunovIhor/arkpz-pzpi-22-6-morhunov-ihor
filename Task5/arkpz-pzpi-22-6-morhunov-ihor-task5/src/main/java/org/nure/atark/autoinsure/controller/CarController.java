package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Retrieve all cars", description = "Fetch a list of all cars in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cars retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class)))
    })
    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }


    @Operation(summary = "Retrieve a car by ID", description = "Fetch a single car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Integer id) {
        try {
            Optional<CarDto> car = carService.getCarById(id);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Create a new car", description = "Add a new car to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PostMapping
    public ResponseEntity<?> createCar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the car to create",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"licensePlate\": \"string\",\n  \"brand\": \"string\",\n  \"model\": \"string\",\n  \"year\": 0,\n  \"userId\": 0,\n \"carTypeId\": 0\n}"),
                            schema = @Schema(implementation = CarDto.class)))
            @RequestBody CarDto carDto) {
        try {
            Optional<CarDto> createdCar = carService.saveCar(carDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Update an existing car", description = "Modify the details of an existing car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAuthority('client')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Integer id,
                                       @RequestBody CarDto carDto) {
        try {
            Optional<CarDto> updatedCar = carService.updateCar(id, carDto);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Retrieve cars by user ID", description = "Fetch a list of cars associated with a specific user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cars retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found or no cars for the user",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCarsByUserId(@PathVariable Integer userId) {
        List<CarDto> cars = carService.getCarsByUserId(userId);
        if (cars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"No cars found for user ID " + userId + "\"}");
        }
        return ResponseEntity.ok(cars);
    }
    @Operation(summary = "Search cars by name", description = "Search for cars by brand or model name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cars found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "No cars found matching the query",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @GetMapping("/search")
    public ResponseEntity<?> searchCarsByName(@RequestParam String query) {
        List<CarDto> cars = carService.searchCarsByName(query);
        if (cars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"No cars found matching the query: " + query + "\"}");
        }
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Delete a car", description = "Remove a car from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PreAuthorize("hasAnyAuthority('administrator', 'client')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
