package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public Optional<CarDto> getCarById(Integer id) {
        return carRepository.findById(id)
                .map(this::convertToDto);
    }

    public CarDto saveCar(Car car) {
        Car savedCar = carRepository.save(car);
        return convertToDto(savedCar);
    }

    // Удаление машины
    public boolean deleteCar(Integer id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<CarDto> updateCar(Integer id, Car carDetails) {
        return carRepository.findById(id)
                .map(car -> {
                    car.setLicensePlate(carDetails.getLicensePlate());
                    car.setBrand(carDetails.getBrand());
                    car.setModel(carDetails.getModel());
                    car.setYear(carDetails.getYear());
                    car.setUser(carDetails.getUser());
                    Car updatedCar = carRepository.save(car);
                    return convertToDto(updatedCar);
                });
    }

    public CarDto convertToDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getUser() != null ? car.getUser().getId() : null);
    }


}
