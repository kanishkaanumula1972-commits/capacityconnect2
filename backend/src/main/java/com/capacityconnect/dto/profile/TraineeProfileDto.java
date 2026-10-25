package com.capacityconnect.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Full trainee profile view - returned by GET /api/trainees/profile
 * and used (partially) for PUT updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeProfileDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;

    private String phone;
    private String location;
    private String profilePhotoUrl;
    private String bio;

    @Builder.Default
    private List<QualificationDto> qualifications = new ArrayList<>();

    @Builder.Default
    private List<WorkExperienceDto> workExperiences = new ArrayList<>();

    @Builder.Default
    private List<TraineeSkillDto> skills = new ArrayList<>();

    @Builder.Default
    private List<String> interests = new ArrayList<>();
}