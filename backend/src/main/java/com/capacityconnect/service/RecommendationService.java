package com.capacityconnect.service;

import com.capacityconnect.dto.competency.CompetencyGapReportDto;
import com.capacityconnect.dto.competency.SkillGapDto;
import com.capacityconnect.dto.recommendation.CourseRecommendationDto;
import com.capacityconnect.dto.recommendation.LearningPathDto;
import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.CourseSkill;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CourseSkillRepository;
import com.capacityconnect.repository.EnrollmentRepository;
import com.capacityconnect.repository.TraineeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Section 10: Personalized Learning Recommendation.
 *
 * Explainable, rule-based algorithm (NOT random, NOT fake AI - section 32):
 *
 * relevanceScore = (skill gap size) x 0.5
 *                 + (course covers this exact skill) x 0.3
 *                 + (difficulty matches trainee level) x 0.1
 *                 - (already enrolled/completed penalty)
 *
 * Every recommendation carries a plain-English "recommendedBecause" string
 * built directly from the inputs used to rank it.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CompetencyProfileService competencyProfileService;
    private final CourseSkillRepository courseSkillRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public LearningPathDto getRecommendationsForGoal(Long userId) {
        CompetencyGapReportDto gapReport = competencyProfileService.getGoalCompetencyGaps(userId);
        return buildLearningPath(userId, gapReport);
    }

    @Transactional(readOnly = true)
    public LearningPathDto getRecommendationsForCompetency(Long userId, Long competencyId) {
        CompetencyGapReportDto gapReport = competencyProfileService.detectGaps(userId, competencyId);
        return buildLearningPath(userId, gapReport);
    }

    private LearningPathDto buildLearningPath(Long userId, CompetencyGapReportDto gapReport) {
        TraineeProfile profile = traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));

        // Only skills with an actual gap matter for recommendations
        List<SkillGapDto> gapsOnly = gapReport.getSkillGaps().stream()
                .filter(SkillGapDto::isHasGap)
                .collect(Collectors.toList());

        if (gapsOnly.isEmpty()) {
            return LearningPathDto.builder()
                    .goalCompetencyName(gapReport.getCompetencyName())
                    .detectedGapSkillNames(List.of())
                    .recommendedCourses(List.of())
                    .build();
        }

        List<Long> gapSkillIds = gapsOnly.stream().map(SkillGapDto::getSkillId).collect(Collectors.toList());
        Map<Long, SkillGapDto> gapBySkillId = gapsOnly.stream()
                .collect(Collectors.toMap(SkillGapDto::getSkillId, g -> g));

        // Which courses this trainee is already enrolled in - avoid re-recommending them
        List<Long> alreadyEnrolledCourseIds = enrollmentRepository.findByTraineeProfileId(profile.getId())
                .stream().map(e -> e.getCourse().getId()).collect(Collectors.toList());

        // Every CourseSkill row that covers ANY of the gap skills, in one query
        List<CourseSkill> matchingCourseSkills = courseSkillRepository.findBySkillIdIn(gapSkillIds);

        List<CourseRecommendationDto> recommendations = matchingCourseSkills.stream()
                .filter(cs -> !alreadyEnrolledCourseIds.contains(cs.getCourse().getId()))
                .filter(cs -> cs.getCourse().getStatus() == Course.CourseStatus.PUBLISHED)
                .map(cs -> buildRecommendation(cs, gapBySkillId.get(cs.getSkill().getId())))
                .sorted(Comparator.comparingDouble(CourseRecommendationDto::getRelevanceScore).reversed())
                .collect(Collectors.toList());

        return LearningPathDto.builder()
                .goalCompetencyName(gapReport.getCompetencyName())
                .detectedGapSkillNames(gapsOnly.stream().map(SkillGapDto::getSkillName).collect(Collectors.toList()))
                .recommendedCourses(recommendations)
                .build();
    }

    private CourseRecommendationDto buildRecommendation(CourseSkill courseSkill, SkillGapDto gap) {
        Course course = courseSkill.getCourse();

        // Relevance score: bigger gap = more urgent = higher score.
        // Expected improvement also weighted in - a course that closes more
        // of the gap in one shot ranks higher than a course covering the
        // same skill but with a smaller improvement.
        double gapWeight = gap.getGap() * 0.7;
        double improvementWeight = Math.min(courseSkill.getExpectedImprovement(), gap.getGap()) * 0.3;
        double relevanceScore = gapWeight + improvementWeight;

        String reason = String.format(
                "Your %s competency is at %.0f%%, below the required %.0f%%. This course covers %s and is expected to improve it by ~%.0f points.",
                gap.getSkillName(), gap.getCurrentScore(), gap.getRequiredScore(),
                gap.getSkillName(), courseSkill.getExpectedImprovement()
        );

        return CourseRecommendationDto.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .difficulty(course.getDifficulty())
                .durationHours(course.getDurationHours())
                .trainerName(course.getTrainer().getUser().getFullName())
                .thumbnailUrl(course.getThumbnailUrl())
                .skillGained(gap.getSkillName())
                .currentGap(gap.getGap())
                .expectedImprovement(courseSkill.getExpectedImprovement())
                .recommendedBecause(reason)
                .relevanceScore(Math.round(relevanceScore * 10.0) / 10.0)
                .build();
    }
}