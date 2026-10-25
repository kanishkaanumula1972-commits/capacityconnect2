package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks a trainee's enrollment in a course, and their progress through it.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_profile_id", "course_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    // 0-100 progress through course content (videos/materials viewed)
    @Builder.Default
    private Double progressPercent = 0.0;
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }

    public enum EnrollmentStatus {
        ENROLLED,
        IN_PROGRESS,
        COMPLETED,
        DROPPED
    }
}