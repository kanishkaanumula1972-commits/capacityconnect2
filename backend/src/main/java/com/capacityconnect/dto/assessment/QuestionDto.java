package com.capacityconnect.dto.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Note: correctOption is included here for the TRAINER-facing create/edit
 * flow. When this DTO is sent to a TRAINEE taking the assessment, the
 * service layer strips correctOption out (see AssessmentAttemptService) -
 * never trust the frontend to simply "not show" the answer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Option A is required")
    private String optionA;

    @NotBlank(message = "Option B is required")
    private String optionB;

    @NotBlank(message = "Option C is required")
    private String optionC;

    @NotBlank(message = "Option D is required")
    private String optionD;

    @Pattern(regexp = "[ABCD]", message = "Correct option must be A, B, C, or D")
    private String correctOption;

    @Builder.Default
    private Integer marks = 1;
}