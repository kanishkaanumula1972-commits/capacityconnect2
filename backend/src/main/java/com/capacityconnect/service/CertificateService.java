package com.capacityconnect.service;

import com.capacityconnect.dto.profile.CertificateDto;
import com.capacityconnect.entity.Certificate;
import com.capacityconnect.entity.Skill;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CertificateRepository;
import com.capacityconnect.repository.SkillRepository;
import com.capacityconnect.repository.TraineeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;

    private TraineeProfile getProfileByUserId(Long userId) {
        return traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));
    }

    @Transactional(readOnly = true)
    public List<CertificateDto> getMyCertificates(Long userId) {
        TraineeProfile profile = getProfileByUserId(userId);
        return certificateRepository.findByTraineeProfileId(profile.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificateDto uploadCertificate(
            Long userId,
            String name,
            String issuingOrganization,
            String issueDate,
            String credentialId,
            List<Long> relatedSkillIds,
            MultipartFile file
    ) {
        TraineeProfile profile = getProfileByUserId(userId);
        String fileUrl = fileStorageService.store(file, "certificates");

        List<Skill> relatedSkills = relatedSkillIds != null
                ? skillRepository.findAllById(relatedSkillIds)
                : List.of();

        Certificate certificate = Certificate.builder()
                .traineeProfile(profile)
                .name(name)
                .issuingOrganization(issuingOrganization)
                .issueDate(issueDate != null && !issueDate.isBlank() ? java.time.LocalDate.parse(issueDate) : null)
                .credentialId(credentialId)
                .fileUrl(fileUrl)
                .relatedSkills(relatedSkills)
                .build();

        certificate = certificateRepository.save(certificate);
        return toDto(certificate);
    }

    @Transactional
    public void deleteCertificate(Long userId, Long certificateId) {
        TraineeProfile profile = getProfileByUserId(userId);
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        if (!certificate.getTraineeProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Certificate not found for this trainee");
        }
        certificateRepository.delete(certificate);
    }

    private CertificateDto toDto(Certificate c) {
        return CertificateDto.builder()
                .id(c.getId())
                .name(c.getName())
                .issuingOrganization(c.getIssuingOrganization())
                .issueDate(c.getIssueDate())
                .credentialId(c.getCredentialId())
                .fileUrl(c.getFileUrl())
                .relatedSkillIds(c.getRelatedSkills().stream().map(Skill::getId).collect(Collectors.toList()))
                .relatedSkillNames(c.getRelatedSkills().stream().map(Skill::getName).collect(Collectors.toList()))
                .build();
    }
}