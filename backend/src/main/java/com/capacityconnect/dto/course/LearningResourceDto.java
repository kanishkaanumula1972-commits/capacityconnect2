package com.capacityconnect.dto.course;

import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.LearningResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResourceDto {
    private Long id;
    private String title;
    private LearningResource.ResourceType type;
    private String fileUrl;
    private String topic;
    private Course.Difficulty difficulty;
    private Long courseId;
    private String courseTitle;
    private String trainerName;
    private LocalDateTime uploadedAt;
}