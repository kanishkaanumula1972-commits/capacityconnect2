package com.capacityconnect.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section 11: Intelligent Trainer Matching output.
 * Example: "Rahul Kumar - 94% Match - Spring Boot: Expert, 8 years, 4.8 rating"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerMatchDto {
    private Long trainerId;
    private String trainerName;
    private String profilePhotoUrl;

    private String skillMatched;
    private String expertiseLevel;
    private Integer yearsOfExperience;
    private Double averageRating;
    private Integer totalRatings;
    private Integer certificationCount;

    // 0-100 overall match percentage
    private Double matchScore;

    // Section 32: explainable reasoning
    private String recommendedBecause;
}