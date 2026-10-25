package com.capacityconnect.dto.assessment;

import lombok.Data;

import java.util.Map;

/**
 * Incoming payload when a trainee submits an assessment.
 * answers: questionId -> selected option ("A"/"B"/"C"/"D")
 */
@Data
public class AttemptSubmissionDto {
    private Map<Long, String> answers;
}