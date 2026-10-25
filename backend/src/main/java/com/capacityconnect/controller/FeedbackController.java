package com.capacityconnect.controller;

import com.capacityconnect.dto.course.FeedbackDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackDto> submitFeedback(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FeedbackDto dto
    ) {
        return ResponseEntity.ok(feedbackService.submitFeedback(principal.getId(), dto));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbackForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(feedbackService.getFeedbackForCourse(courseId));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbackForTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(feedbackService.getFeedbackForTrainer(trainerId));
    }
}