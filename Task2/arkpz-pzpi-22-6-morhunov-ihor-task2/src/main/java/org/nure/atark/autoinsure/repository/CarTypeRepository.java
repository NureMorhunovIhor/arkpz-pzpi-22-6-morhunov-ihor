package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarTypeRepository extends JpaRepository<CarType, Integer> {
}
