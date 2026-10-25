package com.capacityconnect.service;

import com.capacityconnect.dto.admin.AdminDashboardDto;
import com.capacityconnect.dto.admin.AdminUserDto;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Section 3/4/24: Admin user management (approve/reject signups) and
 * the top-level dashboard summary cards.
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final NotificationService notificationService;

    // ---------- User management ----------

    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminUserDto> getPendingUsers() {
        return userRepository.findByStatus(User.AccountStatus.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AdminUserDto approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);

        notificationService.notifyUser(
                userId,
                "Account Approved",
                "Your account has been approved. You can now log in.",
                Notification.NotificationType.ACCOUNT_STATUS
        );

        return toDto(user);
    }

    @Transactional
    public AdminUserDto rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(User.AccountStatus.REJECTED);
        userRepository.save(user);

        notificationService.notifyUser(
                userId,
                "Account Registration Rejected",
                "Your account registration was not approved. Contact admin for details.",
                Notification.NotificationType.ACCOUNT_STATUS
        );

        return toDto(user);
    }

    @Transactional
    public AdminUserDto suspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(User.AccountStatus.SUSPENDED);
        userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public AdminUserDto reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);
        return toDto(user);
    }

    // ---------- Dashboard summary ----------

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardSummary() {
        long totalTrainees = traineeProfileRepository.countBy();
        long totalTrainers = trainerProfileRepository.countBy();
        long activeCourses = courseRepository.countByStatus(Course.CourseStatus.PUBLISHED);
        long totalEnrollments = enrollmentRepository.count();
        long completedEnrollments = enrollmentRepository.countByStatus(Enrollment.EnrollmentStatus.COMPLETED);
        long totalCertificates = certificateRepository.count();
        long totalAssessments = assessmentRepository.count();
        long pendingApprovals = userRepository.findByStatus(User.AccountStatus.PENDING).size();

        double avgScore = assessmentAttemptRepository.findAll().stream()
                .mapToDouble(AssessmentAttempt::getPercentageScore)
                .average()
                .orElse(0.0);

        return AdminDashboardDto.builder()
                .totalTrainees(totalTrainees)
                .totalTrainers(totalTrainers)
                .activeCourses(activeCourses)
                .totalEnrollments(totalEnrollments)
                .completedEnrollments(completedEnrollments)
                .totalCertificates(totalCertificates)
                .totalAssessments(totalAssessments)
                .averageAssessmentScore(Math.round(avgScore * 10.0) / 10.0)
                .pendingApprovals(pendingApprovals)
                .build();
    }

    private AdminUserDto toDto(User u) {
        return AdminUserDto.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .build();
    }
}