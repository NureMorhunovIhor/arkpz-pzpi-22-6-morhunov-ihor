package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findByCarId(Integer carId);

    @Query("SELECT i FROM Incident i WHERE i.car.id IN (SELECT c.id FROM Car c WHERE c.user.id = :userId)")
    List<Incident> findIncidentsByUserId(Integer userId);
}
