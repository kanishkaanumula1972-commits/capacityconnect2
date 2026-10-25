package com.capacityconnect.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProfileDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;

    private String phone;
    private String location;
    private String profilePhotoUrl;
    private String bio;
    private Integer yearsOfExperience;
    private Double averageRating;
    private Integer totalRatings;

    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    @Builder.Default
    private List<QualificationDto> qualifications = new ArrayList<>();

    @Builder.Default
    private List<WorkExperienceDto> workExperiences = new ArrayList<>();
}