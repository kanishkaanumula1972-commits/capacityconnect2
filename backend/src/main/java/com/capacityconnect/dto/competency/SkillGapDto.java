package com.capacityconnect.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row of the Competency Gap Detection output (section 9).
 * Example: skillName="REST API", current=31, required=70, gap=39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapDto {
    private Long skillId;
    private String skillName;
    private Double currentScore;
    private Double requiredScore;
    private Double gap;          // requiredScore - currentScore, floored at 0
    private boolean hasGap;      // true if gap > 0
}