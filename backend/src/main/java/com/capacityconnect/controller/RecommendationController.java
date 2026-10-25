package com.capacityconnect.controller;

import com.capacityconnect.dto.recommendation.LearningPathDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainees/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // Recommendations based on the trainee's currently active goal competency
    @GetMapping
    public ResponseEntity<LearningPathDto> getRecommendationsForGoal(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForGoal(principal.getId()));
    }

    // Recommendations based on any specific competency (e.g. previewing before setting it as goal)
    @GetMapping("/{competencyId}")
    public ResponseEntity<LearningPathDto> getRecommendationsForCompetency(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long competencyId
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForCompetency(principal.getId(), competencyId));
    }
}