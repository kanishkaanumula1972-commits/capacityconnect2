package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.assessment.AssessmentDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    public ResponseEntity<AssessmentDto> createAssessment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AssessmentDto dto
    ) {
        return ResponseEntity.ok(assessmentService.createAssessment(principal.getId(), dto));
    }

    @GetMapping("/my-created")
    public ResponseEntity<List<AssessmentDto>> getMyCreatedAssessments(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(assessmentService.getMyCreatedAssessments(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentDto> getAssessmentForAttempt(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(assessmentService.getAssessmentForAttempt(principal.getId(), id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssessmentDto>> getAssessmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourse(courseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAssessment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        assessmentService.deleteAssessment(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Assessment deleted").success(true).build());
    }
}