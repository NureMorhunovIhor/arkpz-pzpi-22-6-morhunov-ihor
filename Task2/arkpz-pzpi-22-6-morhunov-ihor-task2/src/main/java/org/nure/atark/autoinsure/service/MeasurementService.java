package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.MeasurementDto;
import org.nure.atark.autoinsure.entity.Measurement;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.repository.MeasurementRepository;
import org.nure.atark.autoinsure.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorRepository sensorRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, SensorRepository sensorRepository) {
        this.measurementRepository = measurementRepository;
        this.sensorRepository = sensorRepository;
    }

    public List<MeasurementDto> getAllMeasurements() {
        return measurementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MeasurementDto getMeasurementById(Integer id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Measurement not found with ID: " + id));
        return convertToDto(measurement);
    }

    public MeasurementDto createMeasurement(MeasurementDto measurementDto) {
        Sensor sensor = sensorRepository.findById(measurementDto.getSensorId())
                .orElseThrow(() -> new NoSuchElementException("Sensor not found with ID: " + measurementDto.getSensorId()));

        Measurement measurement = new Measurement();
        measurement.setSensor(sensor);
        measurement.setReadingTime(measurementDto.getReadingTime());
        measurement.setParameterType(measurementDto.getParameterType());
        measurement.setValue(measurementDto.getValue());

        Measurement savedMeasurement = measurementRepository.save(measurement);
        return convertToDto(savedMeasurement);
    }

    public void deleteMeasurement(Integer id) {
        if (!measurementRepository.existsById(id)) {
            throw new NoSuchElementException("Measurement not found with ID: " + id);
        }
        measurementRepository.deleteById(id);
    }

    private MeasurementDto convertToDto(Measurement measurement) {
        return new MeasurementDto(
                measurement.getId(),
                measurement.getSensor().getId(),
                measurement.getReadingTime(),
                measurement.getParameterType(),
                measurement.getValue()
        );
    }
}
