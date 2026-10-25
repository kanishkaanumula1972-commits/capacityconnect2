package com.capacityconnect.service;

import com.capacityconnect.dto.profile.*;
import com.capacityconnect.entity.Qualification;
import com.capacityconnect.entity.TrainerProfile;
import com.capacityconnect.entity.WorkExperience;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.mapper.ProfileMapper;
import com.capacityconnect.repository.QualificationRepository;
import com.capacityconnect.repository.TrainerProfileRepository;
import com.capacityconnect.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainerProfileService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final QualificationRepository qualificationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final ProfileMapper profileMapper;

    private TrainerProfile getProfileByUserId(Long userId) {
        return trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));
    }

    @Transactional(readOnly = true)
    public TrainerProfileDto getProfile(Long userId) {
        TrainerProfile profile = getProfileByUserId(userId);
        return profileMapper.toDto(profile);
    }

    // Also useful for the trainer-matching/public trainer-list features later
    @Transactional(readOnly = true)
    public TrainerProfileDto getProfileById(Long trainerProfileId) {
        TrainerProfile profile = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));
        return profileMapper.toDto(profile);
    }

    @Transactional
    public TrainerProfileDto updateProfile(Long userId, UpdateTrainerProfileRequest request) {
        TrainerProfile profile = getProfileByUserId(userId);

        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getYearsOfExperience() != null) profile.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getSubjects() != null) profile.setSubjects(request.getSubjects());

        trainerProfileRepository.save(profile);
        return getProfile(userId);
    }

    // ---------- Qualifications ----------

    @Transactional
    public QualificationDto addQualification(Long userId, QualificationDto dto) {
        TrainerProfile profile = getProfileByUserId(userId);
        Qualification qualification = Qualification.builder()
                .degree(dto.getDegree())
                .institution(dto.getInstitution())
                .year(dto.getYear())
                .specialization(dto.getSpecialization())
                .trainerProfile(profile)
                .build();
        qualification = qualificationRepository.save(qualification);
        return profileMapper.toDto(qualification);
    }

    @Transactional
    public void deleteQualification(Long userId, Long qualificationId) {
        TrainerProfile profile = getProfileByUserId(userId);
        Qualification qualification = qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found"));

        if (!qualification.getTrainerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Qualification not found for this trainer");
        }
        qualificationRepository.delete(qualification);
    }

    // ---------- Work Experience ----------

    @Transactional
    public WorkExperienceDto addWorkExperience(Long userId, WorkExperienceDto dto) {
        TrainerProfile profile = getProfileByUserId(userId);
        WorkExperience experience = WorkExperience.builder()
                .organization(dto.getOrganization())
                .role(dto.getRole())
                .yearsOfExperience(dto.getYearsOfExperience())
                .responsibilities(dto.getResponsibilities())
                .trainerProfile(profile)
                .build();
        experience = workExperienceRepository.save(experience);
        return profileMapper.toDto(experience);
    }

    @Transactional
    public void deleteWorkExperience(Long userId, Long experienceId) {
        TrainerProfile profile = getProfileByUserId(userId);
        WorkExperience experience = workExperienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));

        if (!experience.getTrainerProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Work experience not found for this trainer");
        }
        workExperienceRepository.delete(experience);
    }
}