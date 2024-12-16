package org.nure.atark.autoinsure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.controller.CarController;
import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@Import(CarControllerTest.TestConfig.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarService carService;

    @Test
    void testGetAllCars() throws Exception {
        var car1 = new CarDto(1, "AB123CD", "Toyota", "Camry", 2020, 1,1);
        var car2 = new CarDto(2, "XY987ZY", "Honda", "Civic", 2019, 2, 1);

        when(carService.getAllCars()).thenReturn(Arrays.asList(car1, car2));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(carService, times(1)).getAllCars();
    }

    @Test
    void testGetCarById() throws Exception {
        var car = new CarDto(1, "AB123CD", "Toyota", "Camry", 2020, 1, 1);

        when(carService.getCarById(1)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("AB123CD"));

        verify(carService, times(1)).getCarById(1);
    }

    @Test
    void testCreateCar() throws Exception {
        var car = new Car();
        car.setLicensePlate("AB123CD");
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setYear(2020);

        var carDto = new CarDto(1, "AB123CD", "Toyota", "Camry", 2020, 1,1);

        when(carService.saveCar(any(CarDto.class))).thenReturn(Optional.of(carDto));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("AB123CD"));

        verify(carService, times(1)).saveCar(any(CarDto.class));
    }

    @Test
    void testDeleteCar() throws Exception {
        when(carService.deleteCar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).deleteCar(1);
    }

    static class TestConfig {
        @Bean
        public CarService carService() {
            return Mockito.mock(CarService.class);
        }
    }
}
