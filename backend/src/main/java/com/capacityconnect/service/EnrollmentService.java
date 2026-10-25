package com.capacityconnect.service;

import com.capacityconnect.dto.course.EnrollmentDto;
import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.Enrollment;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CourseRepository;
import com.capacityconnect.repository.EnrollmentRepository;
import com.capacityconnect.repository.TraineeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final CourseRepository courseRepository;

    private TraineeProfile getProfileByUserId(Long userId) {
        return traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));
    }

    @Transactional
    public EnrollmentDto enroll(Long userId, Long courseId) {
        TraineeProfile profile = getProfileByUserId(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentRepository.existsByTraineeProfileIdAndCourseId(profile.getId(), courseId)) {
            throw new BadRequestException("Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .traineeProfile(profile)
                .course(course)
                .status(Enrollment.EnrollmentStatus.ENROLLED)
                .progressPercent(0.0)
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        return toDto(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDto> getMyEnrollments(Long userId) {
        TraineeProfile profile = getProfileByUserId(userId);
        return enrollmentRepository.findByTraineeProfileId(profile.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDto updateProgress(Long userId, Long courseId, Double progressPercent) {
        TraineeProfile profile = getProfileByUserId(userId);
        Enrollment enrollment = enrollmentRepository.findByTraineeProfileIdAndCourseId(profile.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollment.setProgressPercent(progressPercent);
        if (progressPercent >= 100.0) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        } else if (progressPercent > 0.0) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.IN_PROGRESS);
        }

        enrollment = enrollmentRepository.save(enrollment);
        return toDto(enrollment);
    }

    private EnrollmentDto toDto(Enrollment enrollment) {
        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .status(enrollment.getStatus())
                .progressPercent(enrollment.getProgressPercent())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .build();
    }
}