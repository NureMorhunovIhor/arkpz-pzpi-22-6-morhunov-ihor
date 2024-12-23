package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.IncidentDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.Incident;
import org.nure.atark.autoinsure.entity.Sensor;
import org.nure.atark.autoinsure.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<IncidentDto> getAllIncidents() {
        List<Incident> incidents = incidentRepository.findAll();
        return incidents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<IncidentDto> getIncidentById(Integer id) {
        return incidentRepository.findById(id).map(this::convertToDto);
    }

    public List<IncidentDto> getIncidentsByUserId(Integer userId) {
        List<Incident> incidents = incidentRepository.findIncidentsByUserId(userId);
        return incidents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<IncidentDto> getIncidentsByCarId(Integer carId) {
        List<Incident> incidents = incidentRepository.findByCarId(carId);
        return incidents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public IncidentDto createIncident(IncidentDto incidentDto) {
        Incident incident = new Incident();
        incident.setIncidentDate(incidentDto.getIncidentDate());
        incident.setIncidentType(incidentDto.getIncidentType());
        incident.setDescription(incidentDto.getDescription());

        Car car = new Car();
        car.setId(incidentDto.getCarId());
        incident.setCar(car);

        if (incidentDto.getSensorId() != null) {
            Sensor sensor = new Sensor();
            sensor.setId(incidentDto.getSensorId());
            incident.setSensor(sensor);
        }

        Incident savedIncident = incidentRepository.save(incident);
        return convertToDto(savedIncident);
    }

    public Optional<IncidentDto> updateIncident(Integer id, IncidentDto incidentDto) {
        if (incidentRepository.existsById(id)) {
            Incident incident = new Incident();
            incident.setId(id);
            incident.setIncidentDate(incidentDto.getIncidentDate());
            incident.setIncidentType(incidentDto.getIncidentType());
            incident.setDescription(incidentDto.getDescription());

            Car car = new Car();
            car.setId(incidentDto.getCarId());
            incident.setCar(car);

            if (incidentDto.getSensorId() != null) {
                Sensor sensor = new Sensor();
                sensor.setId(incidentDto.getSensorId());
                incident.setSensor(sensor);
            }

            Incident updatedIncident = incidentRepository.save(incident);
            return Optional.of(convertToDto(updatedIncident));
        }
        return Optional.empty();
    }

    public boolean deleteIncident(Integer id) {
        if (incidentRepository.existsById(id)) {
            incidentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private IncidentDto convertToDto(Incident incident) {
        IncidentDto incidentDto = new IncidentDto();
        incidentDto.setId(incident.getId());
        incidentDto.setIncidentDate(incident.getIncidentDate());
        incidentDto.setIncidentType(incident.getIncidentType());
        incidentDto.setDescription(incident.getDescription());
        if (incident.getCar() != null) {
            incidentDto.setCarId(incident.getCar().getId());
        } else {
            incidentDto.setCarId(null);
        }
        if (incident.getSensor() != null) {
            incidentDto.setSensorId(incident.getSensor().getId());
        }
        return incidentDto;
    }

}
