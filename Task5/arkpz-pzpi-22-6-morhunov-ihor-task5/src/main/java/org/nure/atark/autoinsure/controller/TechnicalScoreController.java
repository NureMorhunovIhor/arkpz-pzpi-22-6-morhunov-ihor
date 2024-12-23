package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.TechnicalScoreDto;
import org.nure.atark.autoinsure.service.TechnicalScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technicalScores")
public class TechnicalScoreController {

    @Autowired
    private TechnicalScoreService technicalScoreService;

    @PostMapping
    public ResponseEntity<?> saveTechnicalScore(@RequestBody TechnicalScoreDto scoreDto) {
        technicalScoreService.saveTechnicalScore(scoreDto);
        return ResponseEntity.ok("Technical score saved successfully");
    }
}

