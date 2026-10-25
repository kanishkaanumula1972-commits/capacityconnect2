package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A skill possessed by a trainee, with a self-declared proficiency level
 * and a computed numeric score (0-100) used by the competency engine.
 *
 * The numeric "score" is what actually powers gap detection and charts
 * (e.g. "Java 82%") - the enum ProficiencyLevel is what the trainee picks
 * in the UI, and we map it to a starting score. The score then gets
 * refined further by assessment results (see AssessmentAttempt -> competency update flow).
 */
@Entity
@Table(name = "trainee_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_profile_id", "skill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel proficiencyLevel;

    // 0-100 numeric score derived from proficiencyLevel initially,
    // then updated by assessment performance over time.
    @Column(nullable = false)
    @Builder.Default
    private Double score = 0.0;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProficiencyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
}