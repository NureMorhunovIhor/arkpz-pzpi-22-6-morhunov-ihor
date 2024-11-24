package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Retrieve a car by ID", description = "Fetch a single car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Integer id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new car", description = "Add a new car to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<CarDto> createCar(@RequestBody Car car) {
        CarDto createdCar = carService.saveCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    @Operation(summary = "Update an existing car", description = "Modify the details of an existing car by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDto.class))),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Integer id, @RequestBody Car carDetails) {
        return carService.updateCar(id, carDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a car", description = "Remove a car from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        if (carService.deleteCar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}