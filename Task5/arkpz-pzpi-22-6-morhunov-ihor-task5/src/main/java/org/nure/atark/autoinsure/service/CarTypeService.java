package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.entity.CarType;
import org.nure.atark.autoinsure.repository.CarTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarTypeService {

    private final CarTypeRepository carTypeRepository;

    public CarTypeService(CarTypeRepository carTypeRepository) {
        this.carTypeRepository = carTypeRepository;
    }

    public List<CarType> getAllCarTypes() {
        return carTypeRepository.findAll();
    }

    public CarType getCarTypeById(Integer id) {
        return carTypeRepository.findById(id).orElseThrow(() ->
                new RuntimeException("CarType with ID " + id + " not found."));
    }

    public CarType createCarType(CarType carType) {
        return carTypeRepository.save(carType);
    }

    public CarType updateCarType(Integer id, CarType carTypeDetails) {
        CarType carType = getCarTypeById(id);
        carType.setCarTypeName(carTypeDetails.getCarTypeName());
        carType.setMinTirePressure(carTypeDetails.getMinTirePressure());
        carType.setMaxTirePressure(carTypeDetails.getMaxTirePressure());
        carType.setMinFuelLevel(carTypeDetails.getMinFuelLevel());
        carType.setMaxFuelLevel(carTypeDetails.getMaxFuelLevel());
        carType.setMinEngineTemp(carTypeDetails.getMinEngineTemp());
        carType.setMaxEngineTemp(carTypeDetails.getMaxEngineTemp());
        return carTypeRepository.save(carType);
    }

    public void deleteCarType(Integer id) {
        CarType carType = getCarTypeById(id);
        carTypeRepository.delete(carType);
    }
}
