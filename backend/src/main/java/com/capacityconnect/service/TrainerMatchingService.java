package com.capacityconnect.service;

import com.capacityconnect.dto.recommendation.TrainerMatchDto;
import com.capacityconnect.entity.TrainerExpertise;
import com.capacityconnect.repository.TrainerCertificationRepository;
import com.capacityconnect.repository.TrainerExpertiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Section 11: Intelligent Trainer Matching.
 *
 * Explainable weighted formula (matches spec exactly):
 *   Match Score = Expertise 40% + Experience 20% + Certification 15%
 *                 + Feedback 15% + Training Performance 10%
 *
 * "Training Performance" is approximated here by the trainer's completion
 * rate isn't tracked as a single number yet, so we currently fold it into
 * the rating-based feedback score - flagged clearly below for future
 * enhancement once TrainerPerformanceAnalytics (section 20) exists.
 */
@Service
@RequiredArgsConstructor
public class TrainerMatchingService {

    private final TrainerExpertiseRepository trainerExpertiseRepository;
    private final TrainerCertificationRepository trainerCertificationRepository;

    private static final double EXPERTISE_WEIGHT = 0.40;
    private static final double EXPERIENCE_WEIGHT = 0.20;
    private static final double CERTIFICATION_WEIGHT = 0.15;
    private static final double FEEDBACK_WEIGHT = 0.15;
    private static final double PERFORMANCE_WEIGHT = 0.10; // see note above

    @Transactional(readOnly = true)
    public List<TrainerMatchDto> findMatchingTrainers(Long skillId, int limit) {
        List<TrainerExpertise> candidates = trainerExpertiseRepository.findBySkillId(skillId);

        List<TrainerMatchDto> matches = candidates.stream()
                .map(this::scoreTrainer)
                .sorted(Comparator.comparingDouble(TrainerMatchDto::getMatchScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return matches;
    }

    private TrainerMatchDto scoreTrainer(TrainerExpertise expertise) {
        var trainer = expertise.getTrainerProfile();

        // 1. Expertise score (0-100): map enum level to a number
        double expertiseScore = switch (expertise.getLevel()) {
            case BEGINNER -> 40.0;
            case INTERMEDIATE -> 65.0;
            case ADVANCED -> 85.0;
            case EXPERT -> 100.0;
        };

        // 2. Experience score (0-100): cap at 10+ years = 100
        int years = trainer.getYearsOfExperience() != null ? trainer.getYearsOfExperience() : 0;
        double experienceScore = Math.min(100.0, (years / 10.0) * 100.0);

        // 3. Certification score (0-100): cap at 5+ certifications = 100
        int certCount = (int) trainerCertificationRepository.countByTrainerProfileId(trainer.getId());
        double certificationScore = Math.min(100.0, (certCount / 5.0) * 100.0);

        // 4. Feedback score (0-100): rating out of 5, scaled to 100
        double avgRating = trainer.getAverageRating() != null ? trainer.getAverageRating() : 0.0;
        double feedbackScore = (avgRating / 5.0) * 100.0;

        // 5. Training performance score - placeholder using feedback as a proxy
        // until section 20's dedicated effectiveness metric feeds in directly.
        double performanceScore = feedbackScore;

        double matchScore =
                (expertiseScore * EXPERTISE_WEIGHT)
                + (experienceScore * EXPERIENCE_WEIGHT)
                + (certificationScore * CERTIFICATION_WEIGHT)
                + (feedbackScore * FEEDBACK_WEIGHT)
                + (performanceScore * PERFORMANCE_WEIGHT);

        String reason = String.format(
                "Recommended because this trainer has %s-level %s expertise, %d years of experience, and a %.1f/5 trainee rating.",
                expertise.getLevel().name().toLowerCase(), expertise.getSkill().getName(), years, avgRating
        );

        return TrainerMatchDto.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getUser().getFullName())
                .profilePhotoUrl(trainer.getProfilePhotoUrl())
                .skillMatched(expertise.getSkill().getName())
                .expertiseLevel(expertise.getLevel().name())
                .yearsOfExperience(years)
                .averageRating(avgRating)
                .totalRatings(trainer.getTotalRatings())
                .certificationCount(certCount)
                .matchScore(Math.round(matchScore * 10.0) / 10.0)
                .recommendedBecause(reason)
                .build();
    }
}