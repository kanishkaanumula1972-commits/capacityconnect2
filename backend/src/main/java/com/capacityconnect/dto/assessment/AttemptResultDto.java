package com.capacityconnect.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 15 + 16: full result of a submitted assessment, including
 * per-question correctness AND the competency update outcome
 * (Before/After/Improvement) if this assessment targets a skill.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttemptResultDto {
    private Long attemptId;
    private Integer marksObtained;
    private Integer totalMarks;
    private Double percentageScore;


    @Builder.Default
    private List<QuestionResultDto> questionResults = new ArrayList<>();

    // Null if this assessment has no targetSkill (not competency-linked)
    private CompetencyUpdateDto competencyUpdate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultDto {
        private Long questionId;
        private String questionText;
        private String selectedOption;
        private String correctOption;
         @JsonProperty("isCorrect")
        private boolean isCorrect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompetencyUpdateDto {
        private String skillName;
        private Double scoreBefore;
        private Double scoreAfter;
        private Double improvement;
        
    }
}