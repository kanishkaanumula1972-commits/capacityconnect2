package com.capacityconnect.controller;

import com.capacityconnect.dto.assessment.AttemptResultDto;
import com.capacityconnect.dto.assessment.AttemptSubmissionDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.AssessmentAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentAttemptController {

    private final AssessmentAttemptService assessmentAttemptService;

    @PostMapping("/{id}/attempt")
    public ResponseEntity<AttemptResultDto> submitAttempt(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody AttemptSubmissionDto submission
    ) {
        return ResponseEntity.ok(assessmentAttemptService.submitAttempt(principal.getId(), id, submission));
    }

    @GetMapping("/my-attempts")
    public ResponseEntity<List<AttemptResultDto>> getMyAttempts(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(assessmentAttemptService.getMyAttempts(principal.getId()));
    }
}