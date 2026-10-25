package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity defining how much of a given skill (as a 0-100 required score)
 * is needed to satisfy a competency.
 *
 * Example row: Competency="Backend Development", Skill="Spring Boot", requiredScore=70
 */
@Entity
@Table(name = "competency_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"competency_id", "skill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetencySkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Required proficiency score (0-100) for this skill within this competency
    @Column(nullable = false)
    private Double requiredScore;
}