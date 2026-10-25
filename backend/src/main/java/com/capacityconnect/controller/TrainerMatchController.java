package com.capacityconnect.controller;

import com.capacityconnect.dto.recommendation.TrainerMatchDto;
import com.capacityconnect.service.TrainerMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainerMatchController {

    private final TrainerMatchingService trainerMatchingService;

    // Section 9/11 flow: "Improve This Skill" -> show matched trainers for that gap skill
    @GetMapping("/api/trainees/trainers/recommended/{skillId}")
    public ResponseEntity<List<TrainerMatchDto>> getMatchingTrainers(
            @PathVariable Long skillId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(trainerMatchingService.findMatchingTrainers(skillId, limit));
    }
}