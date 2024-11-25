package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
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


        Car car = new Car();
        car.setId(sensorDto.getCarId());
        sensor.setCar(car);

        Sensor savedSensor = sensorRepository.save(sensor);
        return convertToDto(savedSensor);
    }

    public Optional<SensorDto> updateSensor(Integer id, SensorDto sensorDto) {
        if (sensorRepository.existsById(id)) {
            Sensor sensor = new Sensor();
            sensor.setId(id);
            sensor.setSensorType(sensorDto.getSensorType());
            sensor.setCurrentState(sensorDto.getCurrentState());
            sensor.setLastUpdate(sensorDto.getLastUpdate());

            Car car = new Car();
            car.setId(sensorDto.getCarId());
            sensor.setCar(car);

            Sensor updatedSensor = sensorRepository.save(sensor);
            return Optional.of(convertToDto(updatedSensor));
        }
        return Optional.empty();
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
        }

        return sensorDto;
    }

}
