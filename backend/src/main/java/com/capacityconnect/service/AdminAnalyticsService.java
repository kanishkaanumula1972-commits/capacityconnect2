package com.capacityconnect.service;

import com.capacityconnect.dto.admin.OrgSkillHealthDto;
import com.capacityconnect.dto.admin.OrganizationalHeatmapDto;
import com.capacityconnect.dto.admin.SkillGapAlertDto;
import com.capacityconnect.entity.CompetencySkill;
import com.capacityconnect.entity.Skill;
import com.capacityconnect.entity.TraineeSkill;
import com.capacityconnect.repository.CompetencySkillRepository;
import com.capacityconnect.repository.SkillRepository;
import com.capacityconnect.repository.TraineeSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Section 18: Organizational Competency Heatmap - the org-wide view of
 * section 8/9's per-trainee logic. Same underlying data (TraineeSkill,
 * CompetencySkill), aggregated across everyone instead of one person.
 */
@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final SkillRepository skillRepository;
    private final TraineeSkillRepository traineeSkillRepository;
    private final CompetencySkillRepository competencySkillRepository;

    @Transactional(readOnly = true)
    public OrganizationalHeatmapDto getHeatmap(int topGapsLimit) {
        List<Skill> allSkills = skillRepository.findAll();

        List<OrgSkillHealthDto> skillHealth = allSkills.stream()
                .map(this::buildSkillHealth)
                .collect(Collectors.toList());

        List<SkillGapAlertDto> topGaps = allSkills.stream()
                .map(this::buildGapAlert)
                .filter(g -> g.getTraineesAffected() > 0)
                .sorted(Comparator.comparingDouble(SkillGapAlertDto::getGap).reversed())
                .limit(topGapsLimit)
                .collect(Collectors.toList());

        return OrganizationalHeatmapDto.builder()
                .skillHealth(skillHealth)
                .topSkillGaps(topGaps)
                .build();
    }

    /**
     * Drill-down for a single skill, e.g. clicking "Cloud Computing" on the
     * heatmap (section 19): "126 trainees require training, average 31%,
     * required 70%".
     */
    @Transactional(readOnly = true)
    public SkillGapAlertDto getSkillGapDetail(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new com.capacityconnect.exception.ResourceNotFoundException("Skill not found"));
        return buildGapAlert(skill);
    }

    private OrgSkillHealthDto buildSkillHealth(Skill skill) {
        List<TraineeSkill> traineeSkills = traineeSkillRepository.findBySkillId(skill.getId());

        double avg = traineeSkills.stream().mapToDouble(TraineeSkill::getScore).average().orElse(0.0);

        return OrgSkillHealthDto.builder()
                .skillId(skill.getId())
                .skillName(skill.getName())
                .averageScore(Math.round(avg * 10.0) / 10.0)
                .traineesTracked((long) traineeSkills.size())
                .build();
    }

    private SkillGapAlertDto buildGapAlert(Skill skill) {
        // The "required" bar for this skill = the highest requiredScore across
        // every competency that lists it (i.e. the toughest bar an
        // organization-wide trainee might be judged against).
        List<CompetencySkill> requirements = competencySkillRepository.findBySkillId(skill.getId());
        double requiredScore = requirements.stream()
                .mapToDouble(CompetencySkill::getRequiredScore)
                .max()
                .orElse(0.0);

        List<TraineeSkill> traineeSkills = traineeSkillRepository.findBySkillId(skill.getId());

        double avgScore = traineeSkills.stream().mapToDouble(TraineeSkill::getScore).average().orElse(0.0);

        long affectedCount = traineeSkills.stream()
                .filter(ts -> ts.getScore() < requiredScore)
                .count();

        double gap = Math.max(0.0, requiredScore - avgScore);

        return SkillGapAlertDto.builder()
                .skillId(skill.getId())
                .skillName(skill.getName())
                .traineesAffected(affectedCount)
                .averageScore(Math.round(avgScore * 10.0) / 10.0)
                .requiredScore(requiredScore)
                .gap(Math.round(gap * 10.0) / 10.0)
                .build();
    }
}