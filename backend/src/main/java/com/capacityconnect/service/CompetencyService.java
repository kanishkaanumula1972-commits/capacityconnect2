package com.capacityconnect.service;

import com.capacityconnect.dto.competency.CompetencyDto;
import com.capacityconnect.dto.competency.CompetencySkillDto;
import com.capacityconnect.entity.Competency;
import com.capacityconnect.entity.CompetencySkill;
import com.capacityconnect.entity.Skill;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CompetencyRepository;
import com.capacityconnect.repository.CompetencySkillRepository;
import com.capacityconnect.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin-facing management of Competency definitions (section 7).
 * This is where "Backend Development requires Java(80%), Spring Boot(70%)..."
 * gets defined.
 */
@Service
@RequiredArgsConstructor
public class CompetencyService {

    private final CompetencyRepository competencyRepository;
    private final CompetencySkillRepository competencySkillRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<CompetencyDto> getAllCompetencies() {
        return competencyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompetencyDto getCompetencyById(Long id) {
        Competency competency = competencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competency not found"));
        return toDto(competency);
    }

    @Transactional
    public CompetencyDto createCompetency(CompetencyDto dto) {
        if (competencyRepository.existsByName(dto.getName())) {
            throw new BadRequestException("A competency with this name already exists");
        }

        Competency competency = Competency.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .build();
        competency = competencyRepository.save(competency);

        attachRequiredSkills(competency, dto.getRequiredSkills());

        return toDto(competencyRepository.findById(competency.getId()).orElseThrow());
    }

    @Transactional
    public CompetencyDto updateCompetency(Long id, CompetencyDto dto) {
        Competency competency = competencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competency not found"));

        competency.setName(dto.getName());
        competency.setDescription(dto.getDescription());
        competency.setCategory(dto.getCategory());
        competencyRepository.save(competency);

        // Replace required-skills list entirely on update - simpler and safe
        // since CompetencySkill rows have no other data pointing to them.
        competencySkillRepository.deleteAll(competencySkillRepository.findByCompetencyId(id));
        attachRequiredSkills(competency, dto.getRequiredSkills());

        return toDto(competencyRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void deleteCompetency(Long id) {
        if (!competencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Competency not found");
        }
        competencyRepository.deleteById(id);
    }

    private void attachRequiredSkills(Competency competency, List<CompetencySkillDto> requiredSkills) {
        if (requiredSkills == null) return;

        for (CompetencySkillDto skillDto : requiredSkills) {
            Skill skill = skillRepository.findById(skillDto.getSkillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillDto.getSkillId()));

            CompetencySkill competencySkill = CompetencySkill.builder()
                    .competency(competency)
                    .skill(skill)
                    .requiredScore(skillDto.getRequiredScore())
                    .build();
            competencySkillRepository.save(competencySkill);
        }
    }

    private CompetencyDto toDto(Competency competency) {
        List<CompetencySkillDto> requiredSkills = competencySkillRepository.findByCompetencyId(competency.getId())
                .stream()
                .map(cs -> CompetencySkillDto.builder()
                        .skillId(cs.getSkill().getId())
                        .skillName(cs.getSkill().getName())
                        .requiredScore(cs.getRequiredScore())
                        .build())
                .collect(Collectors.toList());

        return CompetencyDto.builder()
                .id(competency.getId())
                .name(competency.getName())
                .description(competency.getDescription())
                .category(competency.getCategory())
                .requiredSkills(requiredSkills)
                .build();
    }
}