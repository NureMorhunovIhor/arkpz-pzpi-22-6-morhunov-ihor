package org.nure.atark.autoinsure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.nure.atark.autoinsure.repository.SensorRepository;
import org.nure.atark.autoinsure.service.SensorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private SensorService sensorService;

    @Test
    void testCreateSensor() {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setSensorType("Tire Pressure");
        sensorDto.setCurrentState("Normal");
        sensorDto.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto.setCarId(1);

        Sensor sensor = new Sensor();
        sensor.setSensorType(sensorDto.getSensorType());
        sensor.setCurrentState(sensorDto.getCurrentState());
        sensor.setLastUpdate(sensorDto.getLastUpdate());

        Car car = new Car();
        car.setId(sensorDto.getCarId());
        sensor.setCar(car);

        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        SensorDto createdSensor = sensorService.createSensor(sensorDto);

        assertNotNull(createdSensor);
        assertEquals(sensorDto.getSensorType(), createdSensor.getSensorType());
        assertEquals(sensorDto.getCurrentState(), createdSensor.getCurrentState());
        verify(sensorRepository, times(1)).save(any(Sensor.class));
        verify(carRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateSensor() {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setSensorType("Fuel Level");
        sensorDto.setCurrentState("Full");
        sensorDto.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto.setCarId(1);

        Sensor existingSensor = new Sensor();
        existingSensor.setId(1);
        existingSensor.setSensorType("Fuel Level");
        existingSensor.setCurrentState("Half");
        existingSensor.setLastUpdate(OffsetDateTime.now().toLocalDate());

        Car car = new Car();
        car.setId(1);

        when(sensorRepository.existsById(1)).thenReturn(true);
        when(sensorRepository.findById(1)).thenReturn(Optional.of(existingSensor));
        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<SensorDto> updatedSensor = sensorService.updateSensor(1, sensorDto);

        assertTrue(updatedSensor.isPresent());
        assertEquals(sensorDto.getSensorType(), updatedSensor.get().getSensorType());
        assertEquals(sensorDto.getCurrentState(), updatedSensor.get().getCurrentState());
        verify(sensorRepository, times(1)).findById(1);
        verify(sensorRepository, times(1)).save(any(Sensor.class));
        verify(carRepository, times(1)).findById(1);
    }


    @Test
    void testDeleteSensor() {
        int sensorId = 1;
        when(sensorRepository.existsById(sensorId)).thenReturn(true);

        boolean result = sensorService.deleteSensor(sensorId);

        assertTrue(result);
        verify(sensorRepository, times(1)).deleteById(sensorId);
    }

    @Test
    void testDeleteSensorNotFound() {
        int sensorId = 1;
        when(sensorRepository.existsById(sensorId)).thenReturn(false);

        boolean result = sensorService.deleteSensor(sensorId);

        assertFalse(result);
    }

    @Test
    void testGetAllSensors() {
        List<Sensor> sensors = List.of(new Sensor(), new Sensor());
        when(sensorRepository.findAll()).thenReturn(sensors);

        List<SensorDto> result = sensorService.getAllSensors();

        assertNotNull(result);
        assertEquals(sensors.size(), result.size());
    }

    @Test
    void testGetSensorById() {
        Sensor sensor = new Sensor();
        sensor.setId(1);
        when(sensorRepository.findById(1)).thenReturn(Optional.of(sensor));

        Optional<SensorDto> result = sensorService.getSensorById(1);

        assertTrue(result.isPresent());
        assertEquals(sensor.getId(), result.get().getId());
    }

    @Test
    void testGetSensorByIdNotFound() {
        when(sensorRepository.findById(1)).thenReturn(Optional.empty());

        Optional<SensorDto> result = sensorService.getSensorById(1);

        assertFalse(result.isPresent());
    }
}
