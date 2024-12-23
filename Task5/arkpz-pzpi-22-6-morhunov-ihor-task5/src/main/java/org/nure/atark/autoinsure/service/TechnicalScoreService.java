package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.TechnicalScoreDto;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.entity.TechnicalScore;
import org.nure.atark.autoinsure.repository.CarRepository;
import org.nure.atark.autoinsure.repository.TechnicalScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicalScoreService {

    @Autowired
    private TechnicalScoreRepository technicalScoreRepository;

    @Autowired
    private CarRepository carRepository;

    public void saveTechnicalScore(TechnicalScoreDto scoreDto) {
        TechnicalScore score = new TechnicalScore();

        Car car = carRepository.findById(scoreDto.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car with ID " + scoreDto.getCarId() + " not found"));

        score.setCar(car);
        score.setValue(scoreDto.getValue());
        score.setReadingTime(scoreDto.getReadingTime().toInstant(ZoneOffset.UTC));

        technicalScoreRepository.save(score);
    }

    public Optional<TechnicalScoreDto> getLatestTechnicalScore(Integer carId) {
        List<TechnicalScore> technicalScores = technicalScoreRepository.findByCarIdOrderByReadingTimeDesc(carId);

        return technicalScores.stream()
                .findFirst()
                .map(this::convertToDto);
    }

    private TechnicalScoreDto convertToDto(TechnicalScore technicalScore) {
        TechnicalScoreDto dto = new TechnicalScoreDto();
        dto.setId(technicalScore.getId());
        dto.setCarId(technicalScore.getCar().getId());
        dto.setValue(technicalScore.getValue());
        dto.setReadingTime(LocalDateTime.ofInstant(technicalScore.getReadingTime(), ZoneOffset.UTC));
        return dto;
    }


}
