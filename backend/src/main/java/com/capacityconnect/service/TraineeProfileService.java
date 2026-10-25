package com.capacityconnect.service;

import com.capacityconnect.dto.profile.*;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.mapper.ProfileMapper;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeProfileService {

    private final TraineeProfileRepository traineeProfileRepository;
    private final QualificationRepository qualificationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final TraineeSkillRepository traineeSkillRepository;
    private final SkillRepository skillRepository;
    private final ProfileMapper profileMapper;

    private TraineeProfile getProfileByUserId(Long userId) {
        return traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));
    }

    @Transactional(readOnly = true)
    public TraineeProfileDto getProfile(Long userId) {
        TraineeProfile profile = getProfileByUserId(userId);
        TraineeProfileDto dto = profileMapper.toDto(profile);

        List<TraineeSkillDto> skills = traineeSkillRepository.findByTraineeProfileId(profile.getId())
                .stream()
                .map(profileMapper::toDto)
                .collect(Collectors.toList());
        dto.setSkills(skills);

        return dto;
    }

    @Transactional
    public TraineeProfileDto updateProfile(Long userId, UpdateTraineeProfileRequest request) {
        TraineeProfile profile = getProfileByUserId(userId);

        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getInterests() != null) profile.setInterests(request.getInterests());

        traineeProfileRepository.save(profile);
        return getProfile(userId);
    }

    // ---------- Qualifications ----------

    @Transactional
    public QualificationDto addQualification(Long userId, QualificationDto dto) {
        TraineeProfile profile = getProfileByUserId(userId);
        Qualification qualification = Qualification.builder()
                .degree(dto.getDegree())
                .institution(dto.getInstitution())
                .year(dto.getYear())
                .specialization(dto.getSpecialization())
                .traineeProfile(profile)
                .build();
        qualification = qualificationRepository.save(qualification);
        return profileMapper.toDto(qualification);
    }

    @Transactional
    public void deleteQualification(Long userId, Long qualificationId) {
        TraineeProfile profile = getProfileByUserId(userId);
        Qualification qualification = qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found"));

        if (!qualification.getTraineeProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Qualification not found for this trainee");
        }
        qualificationRepository.delete(qualification);
    }

    // ---------- Work Experience ----------

    @Transactional
    public WorkExperienceDto addWorkExperience(Long userId, WorkExperienceDto dto) {
        TraineeProfile profile = getProfileByUserId(userId);
        WorkExperience experience = WorkExperience.builder()
                .organization(dto.getOrganization())
                .role(dto.getRole())
                .yearsOfExperience(dto.getYearsOfExperience())
                .responsibilities(dto.getResponsibilities())
                .traineeProfile(profile)
                .build();
        experience = workExperienceRepository.save(experience);
        return profileMapper.toDto(experience);
    }

    @Transactional
    public void deleteWorkExperience(Long userId, Long experienceId) {
        TraineeProfile profile = getProfileByUserId(userId);
        WorkExperience experience = workExperienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));

        if (!experience.getTraineeProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Work experience not found for this trainee");
        }
        workExperienceRepository.delete(experience);
    }

    // ---------- Skills ----------

    @Transactional
    public TraineeSkillDto addOrUpdateSkill(Long userId, TraineeSkillDto dto) {
        TraineeProfile profile = getProfileByUserId(userId);
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        TraineeSkill traineeSkill = traineeSkillRepository
                .findByTraineeProfileIdAndSkillId(profile.getId(), skill.getId())
                .orElse(TraineeSkill.builder()
                        .traineeProfile(profile)
                        .skill(skill)
                        .build());

        traineeSkill.setProficiencyLevel(dto.getProficiencyLevel());
        traineeSkill.setScore(mapProficiencyToScore(dto.getProficiencyLevel()));

        traineeSkill = traineeSkillRepository.save(traineeSkill);
        return profileMapper.toDto(traineeSkill);
    }

    @Transactional
    public void deleteSkill(Long userId, Long traineeSkillId) {
        TraineeProfile profile = getProfileByUserId(userId);
        TraineeSkill skill = traineeSkillRepository.findById(traineeSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill entry not found"));

        if (!skill.getTraineeProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Skill entry not found for this trainee");
        }
        traineeSkillRepository.delete(skill);
    }

    /**
     * Maps a self-declared proficiency level to a starting numeric score (0-100).
     * This is the initial value only - assessment attempts refine it later
     * (see AssessmentService's competency-update logic).
     */
    private Double mapProficiencyToScore(TraineeSkill.ProficiencyLevel level) {
        return switch (level) {
            case BEGINNER -> 25.0;
            case INTERMEDIATE -> 50.0;
            case ADVANCED -> 75.0;
            case EXPERT -> 95.0;
        };
    }
}