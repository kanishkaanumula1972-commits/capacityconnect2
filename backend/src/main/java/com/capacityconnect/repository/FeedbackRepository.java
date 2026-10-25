package com.capacityconnect.repository;

import com.capacityconnect.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByCourseId(Long courseId);

    List<Feedback> findByTraineeProfileId(Long traineeProfileId);

    Optional<Feedback> findByTraineeProfileIdAndCourseId(Long traineeProfileId, Long courseId);

    // Powers TrainerProfile.averageRating recalculation - all feedback
    // across all of a trainer's courses
    List<Feedback> findByCourse_TrainerId(Long trainerProfileId);
}