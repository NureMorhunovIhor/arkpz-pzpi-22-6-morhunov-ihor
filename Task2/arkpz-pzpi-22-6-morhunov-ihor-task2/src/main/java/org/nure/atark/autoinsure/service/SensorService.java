package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.nure.atark.autoinsure.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final CarRepository carRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository, CarRepository carRepository) {
        this.sensorRepository = sensorRepository;
        this.carRepository = carRepository;
    }

    public List<SensorDto> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<SensorDto> getSensorById(Integer id) {
        return sensorRepository.findById(id).map(this::convertToDto);
    }

    public SensorDto createSensor(SensorDto sensorDto) {
        Sensor sensor = new Sensor();
        sensor.setSensorType(sensorDto.getSensorType());
        sensor.setCurrentState(sensorDto.getCurrentState());
        sensor.setLastUpdate(sensorDto.getLastUpdate());

        Integer carId = sensorDto.getCarId();
        if (carId == null) {
            throw new IllegalArgumentException("Car ID cannot be null");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NoSuchElementException("Car not found with ID: " + carId));
        sensor.setCar(car);

        Sensor savedSensor = sensorRepository.save(sensor);
        return convertToDto(savedSensor);
    }

    public Optional<SensorDto> updateSensor(Integer id, SensorDto sensorDto) {
        if (sensorRepository.existsById(id)) {
            Sensor sensor = sensorRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Sensor not found"));

            sensor.setSensorType(sensorDto.getSensorType());
            sensor.setCurrentState(sensorDto.getCurrentState());
            sensor.setLastUpdate(sensorDto.getLastUpdate());

            Car car = carRepository.findById(sensorDto.getCarId())
                    .orElseThrow(() -> new NoSuchElementException("Car not found"));
            sensor.setCar(car);

            Sensor updatedSensor = sensorRepository.save(sensor);
            return Optional.of(convertToDto(updatedSensor));
        }
        return Optional.empty();
    }
    public List<SensorDto> getSensorsByCarId(Integer carId) {
        List<Sensor> sensors = sensorRepository.findByCarId(carId);
        return sensors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public boolean deleteSensor(Integer id) {
        if (sensorRepository.existsById(id)) {
            sensorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private SensorDto convertToDto(Sensor sensor) {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setId(sensor.getId());
        sensorDto.setSensorType(sensor.getSensorType());
        sensorDto.setCurrentState(sensor.getCurrentState());
        sensorDto.setLastUpdate(sensor.getLastUpdate());

        if (sensor.getCar() != null) {
            sensorDto.setCarId(sensor.getCar().getId());
        } else {
            sensorDto.setCarId(null);
        }
        return sensorDto;
    }
}
