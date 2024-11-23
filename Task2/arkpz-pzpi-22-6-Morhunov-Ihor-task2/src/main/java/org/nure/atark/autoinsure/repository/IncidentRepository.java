package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
}
