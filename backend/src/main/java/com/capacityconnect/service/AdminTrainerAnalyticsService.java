package com.capacityconnect.service;

import com.capacityconnect.dto.admin.TargetedTrainingProgramDto;
import com.capacityconnect.dto.admin.TrainerPerformanceDto;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Section 20: Trainer Performance Analytics (Admin comparing trainers).
 * Section 19: Admin-initiated Targeted Training Program workflow.
 */
@Service
@RequiredArgsConstructor
public class AdminTrainerAnalyticsService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final CourseRepository courseRepo;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<TrainerPerformanceDto> getAllTrainerPerformance() {
        List<TrainerProfile> trainers = trainerProfileRepository.findAll();

        return trainers.stream()
                .map(this::buildPerformanceDto)
                .sorted(Comparator.comparingDouble(TrainerPerformanceDto::getAverageRating).reversed())
                .collect(Collectors.toList());
    }

    private TrainerPerformanceDto buildPerformanceDto(TrainerProfile trainer) {
        List<Course> courses = courseRepository.findByTrainerId(trainer.getId());
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());

        long courseCount = courses.size();

        long traineeCount = courseIds.stream()
                .mapToLong(enrollmentRepository::countByCourseId)
                .sum();

        long completedCount = courseIds.stream()
                .mapToLong(cid -> enrollmentRepository.countByCourseIdAndStatus(cid, Enrollment.EnrollmentStatus.COMPLETED))
                .sum();

        double completionRate = traineeCount > 0 ? (completedCount * 100.0) / traineeCount : 0.0;

        // Average skill-score improvement from all assessment attempts tied
        // to this trainer's courses (section 20: "Average assessment improvement")
        List<AssessmentAttempt> attempts = courseIds.stream()
                .flatMap(cid -> assessmentAttemptRepository.findByAssessment_CourseId(cid).stream())
                .collect(Collectors.toList());

        double avgImprovement = attempts.stream()
                .filter(a -> a.getSkillScoreBefore() != null && a.getSkillScoreAfter() != null)
                .mapToDouble(a -> a.getSkillScoreAfter() - a.getSkillScoreBefore())
                .average()
                .orElse(0.0);

        return TrainerPerformanceDto.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getUser().getFullName())
                .courseCount(courseCount)
                .traineeCount(traineeCount)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .averageImprovement(Math.round(avgImprovement * 10.0) / 10.0)
                .averageRating(trainer.getAverageRating() != null ? trainer.getAverageRating() : 0.0)
                .totalRatings(trainer.getTotalRatings() != null ? trainer.getTotalRatings().longValue() : 0L)
                .build();
    }

    // ---------- Section 19: Targeted Training Program ----------

    @Transactional
    public void createTargetedTrainingProgram(TargetedTrainingProgramDto dto) {
        Course course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        String deadlineText = dto.getDeadline() != null ? " Deadline: " + dto.getDeadline().toLocalDate() + "." : "";
        String message = (dto.getCustomMessage() != null && !dto.getCustomMessage().isBlank())
                ? dto.getCustomMessage()
                : String.format(
                    "You've been enrolled in a targeted training program: \"%s\" to help close an identified skill gap.%s",
                    course.getTitle(), deadlineText
                );

        for (Long traineeUserId : dto.getTargetTraineeUserIds()) {
            notificationService.notifyUser(
                    traineeUserId,
                    "New Training Program: " + course.getTitle(),
                    message,
                    Notification.NotificationType.TRAINING_PROGRAM
            );
        }
    }
}