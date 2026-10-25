package com.capacityconnect.dto.competency;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetencySkillDto {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    // Output only
    private String skillName;

    @NotNull(message = "Required score is required")
    private Double requiredScore;
}