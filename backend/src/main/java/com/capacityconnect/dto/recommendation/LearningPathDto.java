package com.capacityconnect.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Section 10: "Your Goal: Backend Developer / Detected Gaps: REST API,
 * Spring Boot, Git / Recommended Learning Path: 1. Spring Boot
 * Fundamentals 2. Building REST APIs..."
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathDto {
    private String goalCompetencyName;

    @Builder.Default
    private List<String> detectedGapSkillNames = new ArrayList<>();

    @Builder.Default
    private List<CourseRecommendationDto> recommendedCourses = new ArrayList<>();
}