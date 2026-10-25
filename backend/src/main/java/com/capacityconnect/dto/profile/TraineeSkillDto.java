package com.capacityconnect.dto.profile;

import com.capacityconnect.entity.TraineeSkill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeSkillDto {
    private Long id;

    // Used for output - not needed in the request body (skillId is used instead)
    private String skillName;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Proficiency level is required")
    private TraineeSkill.ProficiencyLevel proficiencyLevel;

    // Computed 0-100 score - output only, ignored on input
    private Double score;
}