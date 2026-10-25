package com.capacityconnect.dto.course;

import com.capacityconnect.entity.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String category;

    @NotNull(message = "Difficulty is required")
    private Course.Difficulty difficulty;

    private Double durationHours;

    private String thumbnailUrl;

    // Output only
    private Long trainerId;
    private String trainerName;

    @Builder.Default
    private List<CourseSkillDto> skillsCovered = new ArrayList<>();

    @Builder.Default
    private List<Long> competencyIds = new ArrayList<>();

    @Builder.Default
    private List<String> competencyNames = new ArrayList<>();

    private Course.CourseStatus status;

    // Output only - populated for trainee-facing views
    private Long enrolledCount;
    private Double averageRating;
}