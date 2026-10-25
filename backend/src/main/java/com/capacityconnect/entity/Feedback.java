package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Trainee feedback submitted after completing a course (section 21).
 * Feeds both TrainerProfile.averageRating and course-level feedback analytics.
 */
@Entity
@Table(name = "feedback", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_profile_id", "course_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Overall rating out of 5
    @Column(nullable = false)
    private Double rating;

    @Column(length = 2000)
    private String comment;

    // Sub-ratings, each out of 5, for more granular analytics
    private Double contentQuality;

    private Double trainerQuality;

    private Double difficultyRating;

    @Column(length = 1000)
    private String suggestions;

    @Column(updatable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}