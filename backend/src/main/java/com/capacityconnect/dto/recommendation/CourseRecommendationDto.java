package com.capacityconnect.dto.recommendation;

import com.capacityconnect.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * One recommended course, tied to the specific skill gap it addresses
 * (section 10). Includes an explainable reason string (section 32).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRecommendationDto {
    private Long courseId;
    private String courseTitle;
    private Course.Difficulty difficulty;
    private Double durationHours;
    private String trainerName;
    private String thumbnailUrl;

    // Which gap this course addresses
    private String skillGained;
    private Double currentGap;
    private Double expectedImprovement;

    // Section 32: explainable recommendation reason
    private String recommendedBecause;

    // Relevance score used purely for ranking (not shown raw to user necessarily,
    // but useful for the frontend to sort/badge "Best Match")
    private Double relevanceScore;
}