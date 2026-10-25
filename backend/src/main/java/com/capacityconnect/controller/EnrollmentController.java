package com.capacityconnect.controller;

import com.capacityconnect.dto.course.EnrollmentDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public ResponseEntity<EnrollmentDto> enroll(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.enroll(principal.getId(), courseId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentDto>> getMyEnrollments(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(principal.getId()));
    }

    @PutMapping("/{courseId}/progress")
    public ResponseEntity<EnrollmentDto> updateProgress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long courseId,
            @RequestParam Double progressPercent
    ) {
        return ResponseEntity.ok(enrollmentService.updateProgress(principal.getId(), courseId, progressPercent));
    }
}