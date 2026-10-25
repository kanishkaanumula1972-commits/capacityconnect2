package com.capacityconnect.dto.course;

import com.capacityconnect.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDto {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    private Enrollment.EnrollmentStatus status;
    private Double progressPercent;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}