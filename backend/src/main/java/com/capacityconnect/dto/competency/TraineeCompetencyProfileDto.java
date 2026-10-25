package com.capacityconnect.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The trainee's own overall skill-by-skill profile (section 8) -
 * Java 82%, SQL 76%, Spring Boot 42%, etc. - independent of any single
 * target competency.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeCompetencyProfileDto {
    @Builder.Default
    private List<SkillScoreDto> skills = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillScoreDto {
        private Long skillId;
        private String skillName;
        private Double score;
    }
}