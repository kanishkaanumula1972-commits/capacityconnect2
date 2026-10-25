package com.capacityconnect.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section 18: one row of "Organizational Competency Health" -
 * e.g. Java 82%, SQL 76%, Cloud 31%, Cybersecurity 27%.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSkillHealthDto {
    private Long skillId;
    private String skillName;
    private Double averageScore;
    private Long traineesTracked; // how many trainees have this skill recorded at all
}