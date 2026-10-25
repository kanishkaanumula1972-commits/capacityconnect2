package com.capacityconnect.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section 20: Trainer Performance Analytics comparison table -
 * Trainer | Rating | Improvement | Completion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerPerformanceDto {
    private Long trainerId;
    private String trainerName;
    private Long courseCount;
    private Long traineeCount;
    private Double completionRate;      // % of enrollments completed
    private Double averageImprovement;  // avg skill score improvement from this trainer's assessments
    private Double averageRating;
    private Long totalRatings;
}