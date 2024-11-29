package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
    List<Maintenance> findByCarId(Integer carId);
}
