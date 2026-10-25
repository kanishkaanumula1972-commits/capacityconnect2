package com.capacityconnect.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section 18: "Top Skill Gaps" - e.g. Cloud Computing, 126 trainees
 * affected, average 31%, required 70%, gap 39%.
 *
 * A trainee is "affected" if their score for this skill is below the
 * threshold required by ANY competency that lists this skill as required
 * (we use the highest required score across competencies as the bar).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapAlertDto {
    private Long skillId;
    private String skillName;
    private Long traineesAffected;
    private Double averageScore;
    private Double requiredScore;
    private Double gap;
}