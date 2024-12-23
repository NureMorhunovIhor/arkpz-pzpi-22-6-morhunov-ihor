package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.dto.TechnicalScoreDto;
import org.nure.atark.autoinsure.entity.TechnicalScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicalScoreRepository extends JpaRepository<TechnicalScore, Integer> {
    List<TechnicalScore> findByCarIdOrderByReadingTimeDesc(Integer carId);
}

