package com.capacityconnect.repository;

import com.capacityconnect.entity.AssessmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {

    List<AssessmentAttempt> findByTraineeProfileId(Long traineeProfileId);

    List<AssessmentAttempt> findByAssessmentId(Long assessmentId);

    Optional<AssessmentAttempt> findTopByTraineeProfileIdAndAssessmentIdOrderByAttemptedAtDesc(
            Long traineeProfileId, Long assessmentId
    );

    boolean existsByTraineeProfileIdAndAssessmentId(Long traineeProfileId, Long assessmentId);

    // Powers Trainer Dashboard "Average trainee score" / "Assessment performance" (section 12)
    List<AssessmentAttempt> findByAssessment_CourseId(Long courseId);
}