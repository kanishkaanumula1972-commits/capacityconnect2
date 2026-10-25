package com.capacityconnect.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Full gap-detection report for a trainee against one target competency
 * (section 9's complete output: overall %, per-skill breakdown, biggest gap).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetencyGapReportDto {
    private Long competencyId;
    private String competencyName;
    private Double overallCurrentScore;  // average of currentScore across required skills
    private Double overallRequiredScore; // average of requiredScore across required skills
    private Double overallGap;

    @Builder.Default
    private List<SkillGapDto> skillGaps = new ArrayList<>();

    // The single largest gap - powers "Your biggest competency gap is REST API" (section 9)
    private SkillGapDto biggestGap;
}