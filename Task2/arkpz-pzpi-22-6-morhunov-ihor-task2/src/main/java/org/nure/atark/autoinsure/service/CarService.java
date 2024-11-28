package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.CarDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.CarType;
import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.nure.atark.autoinsure.repository.CarTypeRepository;  // Добавляем репозиторий для CarType
import org.nure.atark.autoinsure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarTypeRepository carTypeRepository;  // Репозиторий для работы с CarType

    @Autowired
    public CarService(CarRepository carRepository, UserRepository userRepository, CarTypeRepository carTypeRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carTypeRepository = carTypeRepository;  // Инициализируем репозиторий
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

    public Optional<CarDto> saveCar(CarDto carDto) {
        Optional<User> user = carDto.getUserId() != null
                ? userRepository.findById(carDto.getUserId())
                : Optional.empty();

        Car car = new Car();
        car.setLicensePlate(carDto.getLicensePlate());
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());

        // Устанавливаем тип машины через ID
        if (carDto.getCarTypeId() != null) {
            Optional<CarType> carType = carTypeRepository.findById(carDto.getCarTypeId());
            carType.ifPresent(car::setCarType);
        }

        user.ifPresent(car::setUser);

        Car savedCar = carRepository.save(car);
        return Optional.of(convertToDto(savedCar));
    }

    public boolean deleteCar(Integer id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<CarDto> updateCar(Integer id, CarDto carDto) {
        return carRepository.findById(id)
                .map(car -> {
                    car.setLicensePlate(carDto.getLicensePlate());
                    car.setBrand(carDto.getBrand());
                    car.setModel(carDto.getModel());
                    car.setYear(carDto.getYear());

                    if (carDto.getUserId() != null) {
                        Optional<User> user = userRepository.findById(carDto.getUserId());
                        user.ifPresent(car::setUser);
                    } else {
                        car.setUser(null);
                    }

                    // Обновляем тип машины по ID
                    if (carDto.getCarTypeId() != null) {
                        Optional<CarType> carType = carTypeRepository.findById(carDto.getCarTypeId());
                        carType.ifPresent(car::setCarType);
                    }

                    Car updatedCar = carRepository.save(car);
                    return convertToDto(updatedCar);
                });
    }

    private CarDto convertToDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getUser() != null ? car.getUser().getId() : null,
                car.getCarType() != null ? car.getCarType().getId() : null  // Возвращаем ID типа машины
        );
    }
}
