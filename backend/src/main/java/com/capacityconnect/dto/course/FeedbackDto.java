package com.capacityconnect.dto.course;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private Long id;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private String courseTitle;
    private String traineeName; // output only

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    private Double rating;

    private String comment;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double contentQuality;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double trainerQuality;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double difficultyRating;

    private String suggestions;

    private LocalDateTime submittedAt;
}