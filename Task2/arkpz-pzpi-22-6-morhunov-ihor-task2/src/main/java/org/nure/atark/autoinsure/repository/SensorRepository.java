package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    List<Sensor> findByCarId(Integer carId);

}
