package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.MeasurementDto;
import org.nure.atark.autoinsure.entity.CarType;
import org.nure.atark.autoinsure.entity.Measurement;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.repository.CarTypeRepository;
import org.nure.atark.autoinsure.repository.MeasurementRepository;
import org.nure.atark.autoinsure.repository.SensorRepository;
import org.nure.atark.autoinsure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorRepository sensorRepository;
    private final CarTypeRepository carTypeRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository,
                              SensorRepository sensorRepository,
                              CarTypeRepository carTypeRepository,
                              UserRepository userRepository,
                              JavaMailSender mailSender) {
        this.measurementRepository = measurementRepository;
        this.sensorRepository = sensorRepository;
        this.carTypeRepository = carTypeRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
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

    public MeasurementDto createMeasurement(MeasurementDto measurementDto) throws Exception {
        // Получаем информацию о сенсоре и типе машины
        Sensor sensor = sensorRepository.findById(measurementDto.getSensorId())
                .orElseThrow(() -> new NoSuchElementException("Sensor not found with ID: " + measurementDto.getSensorId()));

        Measurement measurement = new Measurement();
        measurement.setSensor(sensor);
        measurement.setReadingTime(measurementDto.getReadingTime());
        measurement.setParameterType(measurementDto.getParameterType());
        measurement.setValue(measurementDto.getValue());

        // Получаем тип машины для проверки значений
        Optional<CarType> carTypeOptional = carTypeRepository.findById(sensor.getCar().getCarType().getId());
        if (!carTypeOptional.isPresent()) {
            throw new NoSuchElementException("Car type not found for sensor ID: " + sensor.getId());
        }

        CarType carType = carTypeOptional.get();

        if (measurementDto.getParameterType().equals("Tire Pressure") &&
                (measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMinTirePressure())) < 0 || 
                 measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMaxTirePressure())) > 0)) {
            sendWarningEmail(sensor.getCar().getUser().getId(), "Tire pressure out of bounds");
        }

        if (measurementDto.getParameterType().equals("Fuel Level") &&
                (measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMinFuelLevel())) < 0 || 
                 measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMaxFuelLevel())) > 0)) {
            sendWarningEmail(sensor.getCar().getUser().getId(), "Fuel level out of bounds");
        }
        if (measurementDto.getParameterType().equals("Engine Temperature") &&
                (measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMinEngineTemp())) < 0 ||
                measurementDto.getValue().compareTo(BigDecimal.valueOf(carType.getMaxEngineTemp())) > 0)) {
            sendWarningEmail(sensor.getCar().getUser().getId(), "Engine temperature out of bounds");
        }

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

    private void sendWarningEmail(Integer userId, String issue) throws MessagingException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            String subject = "Warning: Vehicle Issue Detected";
            String body = "Dear " + user.getFirstName() + ",\n\n" +
                    "We detected an issue with your vehicle: " + issue + ". Please check your vehicle as soon as possible.\n\n" +
                    "Best regards,\nAuto Insure";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
        }
    }
}
