package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Integer> {
    Optional<Rule> findByCarType(String carType);
}
