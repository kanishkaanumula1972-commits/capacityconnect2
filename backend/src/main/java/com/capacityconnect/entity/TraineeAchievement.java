package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Records that a specific trainee has earned a specific achievement.
 * Shown on the trainee's profile (section 23).
 */
@Entity
@Table(name = "trainee_achievements", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_profile_id", "achievement_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(updatable = false)
    private LocalDateTime earnedAt;

    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}