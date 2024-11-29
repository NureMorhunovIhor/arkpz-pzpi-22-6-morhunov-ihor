package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.Maintenance;
import org.nure.atark.autoinsure.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    @Autowired
    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public List<MaintenanceDto> getAllMaintenance() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        return maintenances.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<MaintenanceDto> getMaintenanceById(Integer id) {
        return maintenanceRepository.findById(id).map(this::convertToDto);
    }

    public List<MaintenanceDto> getMaintenanceByCarId(Integer carId) {
        List<Maintenance> maintenances = maintenanceRepository.findByCarId(carId);
        return maintenances.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MaintenanceDto createMaintenance(MaintenanceDto maintenanceDto) {
        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceDate(maintenanceDto.getMaintenanceDate());
        maintenance.setMaintenanceType(maintenanceDto.getMaintenanceType());
        maintenance.setDescription(maintenanceDto.getDescription());
        maintenance.setCost(maintenanceDto.getCost());

        Car car = new Car();
        car.setId(maintenanceDto.getCarId());
        maintenance.setCar(car);

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDto(savedMaintenance);
    }

    public Optional<MaintenanceDto> updateMaintenance(Integer id, MaintenanceDto maintenanceDto) {
        if (maintenanceRepository.existsById(id)) {
            Maintenance maintenance = new Maintenance();
            maintenance.setId(id);
            maintenance.setMaintenanceDate(maintenanceDto.getMaintenanceDate());
            maintenance.setMaintenanceType(maintenanceDto.getMaintenanceType());
            maintenance.setDescription(maintenanceDto.getDescription());
            maintenance.setCost(maintenanceDto.getCost());

            Car car = new Car();
            car.setId(maintenanceDto.getCarId());
            maintenance.setCar(car);

            Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
            return Optional.of(convertToDto(updatedMaintenance));
        }
        return Optional.empty();
    }

    public boolean deleteMaintenance(Integer id) {
        if (maintenanceRepository.existsById(id)) {
            maintenanceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private MaintenanceDto convertToDto(Maintenance maintenance) {
        MaintenanceDto maintenanceDto = new MaintenanceDto();
        maintenanceDto.setId(maintenance.getId());
        maintenanceDto.setMaintenanceDate(maintenance.getMaintenanceDate());
        maintenanceDto.setMaintenanceType(maintenance.getMaintenanceType());
        maintenanceDto.setDescription(maintenance.getDescription());
        maintenanceDto.setCost(maintenance.getCost());

        if (maintenance.getCar() != null) {
            maintenanceDto.setCarId(maintenance.getCar().getId());
        } else {
            maintenanceDto.setCarId(null);
        }
        return maintenanceDto;
    }

}
