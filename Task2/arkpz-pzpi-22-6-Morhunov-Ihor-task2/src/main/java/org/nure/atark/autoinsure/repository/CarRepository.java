package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {

}
