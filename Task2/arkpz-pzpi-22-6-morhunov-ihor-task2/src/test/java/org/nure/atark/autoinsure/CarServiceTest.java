package org.nure.atark.autoinsure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.nure.atark.autoinsure.service.CarService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    private CarRepository carRepository;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        carService = new CarService(carRepository);
    }

    @Test
    void testGetAllCars() {
        var user = new User();  // Создайте объект User
        user.setId(1);  // Установите ID для пользователя

        var car1 = new Car();
        car1.setId(1);
        car1.setLicensePlate("AB123CD");
        car1.setBrand("Toyota");
        car1.setModel("Camry");
        car1.setYear(2020);
        car1.setUser(user);

        var car2 = new Car();
        car2.setId(2);
        car2.setLicensePlate("XY987ZY");
        car2.setBrand("Honda");
        car2.setModel("Civic");
        car2.setYear(2019);
        car2.setUser(user);

        when(carRepository.findAll()).thenReturn(Arrays.asList(car1, car2));

        List<CarDto> cars = carService.getAllCars();

        assertEquals(2, cars.size());
        assertEquals("AB123CD", cars.get(0).getLicensePlate());
        verify(carRepository, times(1)).findAll();
    }


    @Test
    void testGetCarById_CarExists() {
        var car = new Car();
        car.setId(1);
        car.setLicensePlate("AB123CD");
        car.setBrand("Toyota");

        when(carRepository.findById(1)).thenReturn(Optional.of(car));

        Optional<CarDto> foundCar = carService.getCarById(1);

        assertTrue(foundCar.isPresent());
        assertEquals("AB123CD", foundCar.get().getLicensePlate());
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    void testGetCarById_CarDoesNotExist() {
        when(carRepository.findById(1)).thenReturn(Optional.empty());

        Optional<CarDto> foundCar = carService.getCarById(1);

        assertTrue(foundCar.isEmpty());
    }

    @Test
    void testSaveCar() {
        var user = mock(User.class);
        when(user.getId()).thenReturn(1);

        var car = new Car();
        car.setLicensePlate("AB123CD");
        car.setBrand("Toyota");
        car.setUser(user);

        var savedCar = new Car();
        savedCar.setId(1);
        savedCar.setLicensePlate("AB123CD");
        savedCar.setBrand("Toyota");
        savedCar.setUser(user);

        when(carRepository.save(car)).thenReturn(savedCar);

        CarDto carDto = carService.saveCar(car);

        assertNotNull(carDto);
        assertEquals(1, carDto.getId());
        assertEquals("AB123CD", carDto.getLicensePlate());
        verify(carRepository, times(1)).save(car);
    }


    @Test
    void testDeleteCar() {
        when(carRepository.existsById(1)).thenReturn(true);
        doNothing().when(carRepository).deleteById(1);

        boolean isDeleted = carService.deleteCar(1);

        assertTrue(isDeleted);
        verify(carRepository, times(1)).existsById(1);
        verify(carRepository, times(1)).deleteById(1);
    }
}
