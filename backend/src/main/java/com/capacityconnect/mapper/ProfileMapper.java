package com.capacityconnect.mapper;

import com.capacityconnect.dto.profile.*;
import com.capacityconnect.entity.Qualification;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.entity.TraineeSkill;
import com.capacityconnect.entity.TrainerProfile;
import com.capacityconnect.entity.WorkExperience;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts between entities and DTOs for the profile module.
 * Kept as plain methods (no MapStruct) to stay dependency-light and
 * transparent for a hackathon timeline - easy to read, easy to debug.
 */
@Component
public class ProfileMapper {

    public QualificationDto toDto(Qualification q) {
        return QualificationDto.builder()
                .id(q.getId())
                .degree(q.getDegree())
                .institution(q.getInstitution())
                .year(q.getYear())
                .specialization(q.getSpecialization())
                .build();
    }

    public WorkExperienceDto toDto(WorkExperience w) {
        return WorkExperienceDto.builder()
                .id(w.getId())
                .organization(w.getOrganization())
                .role(w.getRole())
                .yearsOfExperience(w.getYearsOfExperience())
                .responsibilities(w.getResponsibilities())
                .build();
    }

    public TraineeSkillDto toDto(TraineeSkill s) {
        return TraineeSkillDto.builder()
                .id(s.getId())
                .skillId(s.getSkill().getId())
                .skillName(s.getSkill().getName())
                .proficiencyLevel(s.getProficiencyLevel())
                .score(s.getScore())
                .build();
    }

    public TraineeProfileDto toDto(TraineeProfile profile) {
        List<QualificationDto> qualifications = profile.getQualifications().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        List<WorkExperienceDto> workExperiences = profile.getWorkExperiences().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return TraineeProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .bio(profile.getBio())
                .qualifications(qualifications)
                .workExperiences(workExperiences)
                .interests(new java.util.ArrayList<>(profile.getInterests()))
                .build();
        // Note: `skills` list is populated separately by the service layer
        // (it needs TraineeSkillRepository, which this pure mapper doesn't have)
    }

    public TrainerProfileDto toDto(TrainerProfile profile) {
        List<QualificationDto> qualifications = profile.getQualifications().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        List<WorkExperienceDto> workExperiences = profile.getWorkExperiences().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return TrainerProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .bio(profile.getBio())
                .yearsOfExperience(profile.getYearsOfExperience())
                .averageRating(profile.getAverageRating())
                .totalRatings(profile.getTotalRatings())
                .subjects(new java.util.ArrayList<>(profile.getSubjects()))
                .qualifications(qualifications)
                .workExperiences(workExperiences)
                .build();
    }
}