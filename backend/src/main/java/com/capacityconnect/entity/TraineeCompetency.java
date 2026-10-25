package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks a trainee's overall progress toward a specific target competency
 * (e.g. "Backend Developer"). The actual per-skill breakdown is computed
 * on-the-fly by the CompetencyService (comparing TraineeSkill scores against
 * CompetencySkill required scores) - this entity just records which
 * competencies a trainee is pursuing and their last-computed overall %,
 * so we don't need to recalculate from scratch on every dashboard load.
 */
@Entity
@Table(name = "trainee_competencies", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_profile_id", "competency_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;

    // Overall % match toward this competency, averaged across required skills
    @Builder.Default
    private Double overallScore = 0.0;

    // Whether the trainee has marked this as their active target/goal competency
    @Builder.Default
    private boolean isTargetGoal = false;

    private LocalDateTime lastCalculatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastCalculatedAt = LocalDateTime.now();
    }
}