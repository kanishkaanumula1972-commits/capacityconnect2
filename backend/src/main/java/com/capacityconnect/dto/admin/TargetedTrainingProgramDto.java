package com.capacityconnect.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Section 19: Admin creates a targeted training program from a detected
 * organizational skill gap - selects a course, trainer, target trainees,
 * and a deadline, then publishes notifications to all of them.
 */
@Data
public class TargetedTrainingProgramDto {
    @NotNull(message = "Course ID is required")
    private Long courseId;

    // Optional - informational only for now (no separate assignment table);
    // the notification message references the trainer, actual pairing
    // happens naturally since the course already has one trainer attached.
    private Long trainerId;

    @NotNull(message = "At least one target trainee is required")
    private List<Long> targetTraineeUserIds;

    private LocalDateTime deadline;

    private String customMessage;
}