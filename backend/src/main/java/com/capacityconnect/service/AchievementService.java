package com.capacityconnect.service;

import com.capacityconnect.dto.notification.AchievementDto;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.AchievementRepository;
import com.capacityconnect.repository.TraineeAchievementRepository;
import com.capacityconnect.repository.TraineeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Section 23: Achievement/badge system. Rule-based awarding (no fake AI,
 * per section 32) - checkAndAwardSkillMastery/checkAndAwardAssessmentScore
 * are called by other services (AssessmentAttemptService, EnrollmentService)
 * right after the relevant event, so badges appear immediately.
 */
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final TraineeAchievementRepository traineeAchievementRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final NotificationService notificationService;

    // ---------- Admin management ----------

    @Transactional(readOnly = true)
    public List<AchievementDto> getAllAchievements() {
        return achievementRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AchievementDto createAchievement(AchievementDto dto) {
        Achievement achievement = Achievement.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .criteria(dto.getCriteria())
                .criteriaThreshold(dto.getCriteriaThreshold())
                .build();
        achievement = achievementRepository.save(achievement);
        return toDto(achievement);
    }

    @Transactional
    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Achievement not found");
        }
        achievementRepository.deleteById(id);
    }

    // ---------- Trainee-facing ----------

    @Transactional(readOnly = true)
    public List<AchievementDto> getMyAchievements(Long userId) {
        TraineeProfile profile = traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));

        return traineeAchievementRepository.findByTraineeProfileId(profile.getId())
                .stream()
                .map(ta -> {
                    AchievementDto dto = toDto(ta.getAchievement());
                    dto.setEarnedAt(ta.getEarnedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ---------- Rule-based auto-awarding ----------

    /**
     * Called after a skill score update (assessment or manual). Checks all
     * SKILL_MASTERY achievements and awards any the trainee now qualifies for.
     */
    @Transactional
    public void checkAndAwardSkillMastery(TraineeProfile profile, double newSkillScore) {
        List<Achievement> masteryAchievements = achievementRepository.findAll().stream()
                .filter(a -> a.getCriteria() == Achievement.AchievementCriteria.SKILL_MASTERY)
                .filter(a -> newSkillScore >= a.getCriteriaThreshold())
                .collect(Collectors.toList());

        for (Achievement achievement : masteryAchievements) {
            awardIfNotAlreadyEarned(profile, achievement);
        }
    }

    /**
     * Called after an assessment attempt. Checks ASSESSMENT_HIGH_SCORE achievements.
     */
    @Transactional
    public void checkAndAwardAssessmentScore(TraineeProfile profile, double percentageScore) {
        List<Achievement> scoreAchievements = achievementRepository.findAll().stream()
                .filter(a -> a.getCriteria() == Achievement.AchievementCriteria.ASSESSMENT_HIGH_SCORE)
                .filter(a -> percentageScore >= a.getCriteriaThreshold())
                .collect(Collectors.toList());

        for (Achievement achievement : scoreAchievements) {
            awardIfNotAlreadyEarned(profile, achievement);
        }
    }

    /**
     * Called after course completion count changes.
     */
    @Transactional
    public void checkAndAwardCourseCompletion(TraineeProfile profile, long completedCourseCount) {
        List<Achievement> completionAchievements = achievementRepository.findAll().stream()
                .filter(a -> a.getCriteria() == Achievement.AchievementCriteria.COURSE_COMPLETION_COUNT)
                .filter(a -> completedCourseCount >= a.getCriteriaThreshold())
                .collect(Collectors.toList());

        for (Achievement achievement : completionAchievements) {
            awardIfNotAlreadyEarned(profile, achievement);
        }
    }

    private void awardIfNotAlreadyEarned(TraineeProfile profile, Achievement achievement) {
        if (traineeAchievementRepository.existsByTraineeProfileIdAndAchievementId(profile.getId(), achievement.getId())) {
            return; // already has this badge
        }

        TraineeAchievement traineeAchievement = TraineeAchievement.builder()
                .traineeProfile(profile)
                .achievement(achievement)
                .build();
        traineeAchievementRepository.save(traineeAchievement);

        notificationService.notifyUser(
                profile.getUser().getId(),
                "Achievement Unlocked: " + achievement.getName(),
                achievement.getDescription(),
                Notification.NotificationType.ACHIEVEMENT_EARNED
        );
    }

    private AchievementDto toDto(Achievement a) {
        return AchievementDto.builder()
                .id(a.getId())
                .name(a.getName())
                .description(a.getDescription())
                .icon(a.getIcon())
                .criteria(a.getCriteria())
                .criteriaThreshold(a.getCriteriaThreshold())
                .build();
    }
}