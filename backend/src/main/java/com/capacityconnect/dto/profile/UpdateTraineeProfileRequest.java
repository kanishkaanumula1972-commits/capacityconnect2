package com.capacityconnect.dto.profile;

import lombok.Data;

import java.util.List;

/**
 * Payload for PUT /api/trainees/profile - only the editable basic fields.
 * Qualifications/WorkExperience/Skills/Interests are managed via their
 * own dedicated endpoints (add/update/delete), not through this bulk update.
 */
@Data
public class UpdateTraineeProfileRequest {
    private String phone;
    private String location;
    private String bio;
    private List<String> interests;
}