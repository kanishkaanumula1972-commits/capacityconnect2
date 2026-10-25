package com.capacityconnect.service;

import com.capacityconnect.dto.competency.*;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements sections 8 & 9: the trainee's own competency profile
 * (skill-by-skill % scores) and the Competency Gap Detection engine
 * (comparing a trainee against a target competency's requirements).
 *
 * This is the single most important service in the whole platform -
 * everything else (recommendations, trainer matching) consumes its output.
 */
@Service
@RequiredArgsConstructor
public class CompetencyProfileService {

    private final TraineeProfileRepository traineeProfileRepository;
    private final TraineeSkillRepository traineeSkillRepository;
    private final CompetencyRepository competencyRepository;
    private final CompetencySkillRepository competencySkillRepository;
    private final TraineeCompetencyRepository traineeCompetencyRepository;

    private TraineeProfile getProfileByUserId(Long userId) {
        return traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));
    }

    /**
     * Section 8: "Your Competency Profile" - Java 82%, SQL 76%, etc.
     * Simply all of the trainee's own skill scores, no comparison involved.
     */
    @Transactional(readOnly = true)
    public TraineeCompetencyProfileDto getCompetencyProfile(Long userId) {
        TraineeProfile profile = getProfileByUserId(userId);

        List<TraineeCompetencyProfileDto.SkillScoreDto> skills = traineeSkillRepository
                .findByTraineeProfileId(profile.getId())
                .stream()
                .map(ts -> TraineeCompetencyProfileDto.SkillScoreDto.builder()
                        .skillId(ts.getSkill().getId())
                        .skillName(ts.getSkill().getName())
                        .score(ts.getScore())
                        .build())
                .collect(Collectors.toList());

        return TraineeCompetencyProfileDto.builder().skills(skills).build();
    }

    /**
     * Section 9: Competency Gap Detection - the core algorithm.
     *
     * Steps:
     * 1. Load the target competency's required skills (Competency -> CompetencySkill).
     * 2. Load the trainee's current scores for those same skills (TraineeSkill).
     *    If the trainee has never recorded a skill at all, treat their score as 0.
     * 3. For each required skill: gap = max(0, required - current).
     * 4. Overall score = average across all required skills (both sides).
     * 5. Biggest gap = the skill with the largest gap value.
     */
    @Transactional
    public CompetencyGapReportDto detectGaps(Long userId, Long competencyId) {
        TraineeProfile profile = getProfileByUserId(userId);

        Competency competency = competencyRepository.findById(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Competency not found"));

        List<CompetencySkill> requiredSkills = competencySkillRepository.findByCompetencyId(competencyId);
        if (requiredSkills.isEmpty()) {
            throw new ResourceNotFoundException("This competency has no required skills defined yet");
        }

        // Build a lookup of the trainee's current scores, keyed by skill ID
        Map<Long, Double> currentScoresBySkillId = traineeSkillRepository
                .findByTraineeProfileId(profile.getId())
                .stream()
                .collect(Collectors.toMap(ts -> ts.getSkill().getId(), TraineeSkill::getScore));

        List<SkillGapDto> skillGaps = requiredSkills.stream()
                .map(reqSkill -> {
                    Long skillId = reqSkill.getSkill().getId();
                    double current = currentScoresBySkillId.getOrDefault(skillId, 0.0);
                    double required = reqSkill.getRequiredScore();
                    double gap = Math.max(0.0, required - current);

                    return SkillGapDto.builder()
                            .skillId(skillId)
                            .skillName(reqSkill.getSkill().getName())
                            .currentScore(current)
                            .requiredScore(required)
                            .gap(gap)
                            .hasGap(gap > 0)
                            .build();
                })
                .collect(Collectors.toList());

        double overallCurrent = skillGaps.stream().mapToDouble(SkillGapDto::getCurrentScore).average().orElse(0.0);
        double overallRequired = skillGaps.stream().mapToDouble(SkillGapDto::getRequiredScore).average().orElse(0.0);
        double overallGap = Math.max(0.0, overallRequired - overallCurrent);

        SkillGapDto biggestGap = skillGaps.stream()
                .max(Comparator.comparingDouble(SkillGapDto::getGap))
                .orElse(null);

        // Cache the overall score onto TraineeCompetency for fast dashboard reads later
        TraineeCompetency traineeCompetency = traineeCompetencyRepository
                .findByTraineeProfileIdAndCompetencyId(profile.getId(), competencyId)
                .orElse(TraineeCompetency.builder()
                        .traineeProfile(profile)
                        .competency(competency)
                        .build());
        traineeCompetency.setOverallScore(overallCurrent);
        traineeCompetencyRepository.save(traineeCompetency);

        return CompetencyGapReportDto.builder()
                .competencyId(competency.getId())
                .competencyName(competency.getName())
                .overallCurrentScore(round(overallCurrent))
                .overallRequiredScore(round(overallRequired))
                .overallGap(round(overallGap))
                .skillGaps(skillGaps)
                .biggestGap(biggestGap)
                .build();
    }

    /**
     * Marks a competency as the trainee's active goal (section 29 demo:
     * "Your Goal: Backend Developer"). Only one goal active at a time.
     */
    @Transactional
    public void setGoalCompetency(Long userId, Long competencyId) {
        TraineeProfile profile = getProfileByUserId(userId);
        Competency competency = competencyRepository.findById(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Competency not found"));

        // Unset any existing goal
        traineeCompetencyRepository.findByTraineeProfileIdAndIsTargetGoalTrue(profile.getId())
                .ifPresent(existing -> {
                    existing.setTargetGoal(false);
                    traineeCompetencyRepository.save(existing);
                });

        TraineeCompetency traineeCompetency = traineeCompetencyRepository
                .findByTraineeProfileIdAndCompetencyId(profile.getId(), competencyId)
                .orElse(TraineeCompetency.builder()
                        .traineeProfile(profile)
                        .competency(competency)
                        .build());
        traineeCompetency.setTargetGoal(true);
        traineeCompetencyRepository.save(traineeCompetency);
    }

    @Transactional(readOnly = true)
    public CompetencyGapReportDto getGoalCompetencyGaps(Long userId) {
        TraineeProfile profile = getProfileByUserId(userId);
        TraineeCompetency goal = traineeCompetencyRepository
                .findByTraineeProfileIdAndIsTargetGoalTrue(profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No goal competency set yet"));

        return detectGaps(userId, goal.getCompetency().getId());
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}