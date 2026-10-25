package com.capacityconnect.dto.course;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSkillDto {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    private String skillName; // output only

    @Builder.Default
    private Double expectedImprovement = 20.0;
}