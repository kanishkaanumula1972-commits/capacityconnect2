package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.admin.TargetedTrainingProgramDto;
import com.capacityconnect.dto.admin.TrainerPerformanceDto;
import com.capacityconnect.service.AdminTrainerAnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTrainerAnalyticsController {

    private final AdminTrainerAnalyticsService adminTrainerAnalyticsService;

    // Section 20: Trainer Performance comparison table
    @GetMapping("/trainer-performance")
    public ResponseEntity<List<TrainerPerformanceDto>> getTrainerPerformance() {
        return ResponseEntity.ok(adminTrainerAnalyticsService.getAllTrainerPerformance());
    }

    // Section 19: create + publish a targeted training program
    @PostMapping("/training-programs")
    public ResponseEntity<MessageResponse> createTrainingProgram(
            @Valid @RequestBody TargetedTrainingProgramDto dto
    ) {
        adminTrainerAnalyticsService.createTargetedTrainingProgram(dto);
        return ResponseEntity.ok(
                MessageResponse.builder().message("Training program published and notifications sent").success(true).build()
        );
    }
}