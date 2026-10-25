package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.competency.CompetencyGapReportDto;
import com.capacityconnect.dto.competency.TraineeCompetencyProfileDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.CompetencyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeCompetencyController {

    private final CompetencyProfileService competencyProfileService;

    // Section 8: "Your Competency Profile" - Java 82%, SQL 76%, etc.
    @GetMapping("/competencies")
    public ResponseEntity<TraineeCompetencyProfileDto> getCompetencyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(competencyProfileService.getCompetencyProfile(principal.getId()));
    }

    // Section 9: Gap detection against a specific target competency
    @GetMapping("/gaps/{competencyId}")
    public ResponseEntity<CompetencyGapReportDto> detectGaps(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long competencyId
    ) {
        return ResponseEntity.ok(competencyProfileService.detectGaps(principal.getId(), competencyId));
    }

    // Section 29 demo flow: gaps against the trainee's currently set goal competency
    @GetMapping("/gaps")
    public ResponseEntity<CompetencyGapReportDto> getGoalGaps(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(competencyProfileService.getGoalCompetencyGaps(principal.getId()));
    }

    @PostMapping("/goal/{competencyId}")
    public ResponseEntity<MessageResponse> setGoalCompetency(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long competencyId
    ) {
        competencyProfileService.setGoalCompetency(principal.getId(), competencyId);
        return ResponseEntity.ok(MessageResponse.builder().message("Goal competency updated").success(true).build());
    }
}