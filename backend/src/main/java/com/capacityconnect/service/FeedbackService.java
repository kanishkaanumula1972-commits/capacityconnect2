package com.capacityconnect.service;

import com.capacityconnect.dto.course.FeedbackDto;
import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.Feedback;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.entity.TrainerProfile;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CourseRepository;
import com.capacityconnect.repository.FeedbackRepository;
import com.capacityconnect.repository.TraineeProfileRepository;
import com.capacityconnect.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final CourseRepository courseRepository;
    private final TrainerProfileRepository trainerProfileRepository;

    @Transactional
    public FeedbackDto submitFeedback(Long userId, FeedbackDto dto) {
        TraineeProfile profile = traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (feedbackRepository.findByTraineeProfileIdAndCourseId(profile.getId(), course.getId()).isPresent()) {
            throw new BadRequestException("You have already submitted feedback for this course");
        }

        Feedback feedback = Feedback.builder()
                .traineeProfile(profile)
                .course(course)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .contentQuality(dto.getContentQuality())
                .trainerQuality(dto.getTrainerQuality())
                .difficultyRating(dto.getDifficultyRating())
                .suggestions(dto.getSuggestions())
                .build();
        feedback = feedbackRepository.save(feedback);

        recalculateTrainerRating(course.getTrainer().getId());

        return toDto(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackDto> getFeedbackForCourse(Long courseId) {
        return feedbackRepository.findByCourseId(courseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackDto> getFeedbackForTrainer(Long trainerProfileId) {
        return feedbackRepository.findByCourse_TrainerId(trainerProfileId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Recomputes TrainerProfile.averageRating/totalRatings from scratch
     * across ALL of the trainer's courses. Called whenever new feedback
     * arrives - keeps the denormalized rating (used everywhere in trainer
     * matching, section 11) always in sync.
     */
    private void recalculateTrainerRating(Long trainerProfileId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        List<Feedback> allFeedback = feedbackRepository.findByCourse_TrainerId(trainerProfileId);

        double avg = allFeedback.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);

        trainer.setAverageRating(Math.round(avg * 10.0) / 10.0);
        trainer.setTotalRatings(allFeedback.size());
        trainerProfileRepository.save(trainer);
    }

    private FeedbackDto toDto(Feedback f) {
        return FeedbackDto.builder()
                .id(f.getId())
                .courseId(f.getCourse().getId())
                .courseTitle(f.getCourse().getTitle())
                .traineeName(f.getTraineeProfile().getUser().getFullName())
                .rating(f.getRating())
                .comment(f.getComment())
                .contentQuality(f.getContentQuality())
                .trainerQuality(f.getTrainerQuality())
                .difficultyRating(f.getDifficultyRating())
                .suggestions(f.getSuggestions())
                .submittedAt(f.getSubmittedAt())
                .build();
    }
}