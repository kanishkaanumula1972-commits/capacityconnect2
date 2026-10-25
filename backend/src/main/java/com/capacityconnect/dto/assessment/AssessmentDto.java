package com.capacityconnect.dto.assessment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long courseId;
    private String courseTitle;

    private Long targetSkillId;
    private String targetSkillName;

    private Long targetCompetencyId;
    private String targetCompetencyName;

    private Integer totalMarks;
    private Integer durationMinutes;
    private LocalDateTime deadline;

    @Builder.Default
    private List<QuestionDto> questions = new ArrayList<>();

    // Output-only, for trainee list views
    private Boolean alreadyAttempted;
}