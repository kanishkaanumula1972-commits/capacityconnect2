package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A trainer's proficiency in a specific skill - the core data behind
 * Intelligent Trainer Matching (section 11). Example: Rahul Kumar ->
 * Spring Boot -> EXPERT.
 */
@Entity
@Table(name = "trainer_expertise", uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_profile_id", "skill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_profile_id", nullable = false)
    private TrainerProfile trainerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpertiseLevel level;

    public enum ExpertiseLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
}