package com.capacityconnect.repository;

import com.capacityconnect.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByTraineeProfileId(Long traineeProfileId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByTraineeProfileIdAndCourseId(Long traineeProfileId, Long courseId);

    boolean existsByTraineeProfileIdAndCourseId(Long traineeProfileId, Long courseId);

    long countByStatus(Enrollment.EnrollmentStatus status);

    long countByCourseId(Long courseId);

    // Powers Trainer Dashboard "Active Trainees" (section 12)
    long countByCourseIdAndStatus(Long courseId, Enrollment.EnrollmentStatus status);
}