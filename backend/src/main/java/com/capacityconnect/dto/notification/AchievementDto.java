package com.capacityconnect.dto.notification;

import com.capacityconnect.entity.Achievement;
import jakarta.validation.constraints.NotBlank;
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
public class AchievementDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private String icon;

    @NotNull(message = "Criteria is required")
    private Achievement.AchievementCriteria criteria;

    private Double criteriaThreshold;

    // Output only, populated when listing a trainee's earned achievements
    private LocalDateTime earnedAt;
}