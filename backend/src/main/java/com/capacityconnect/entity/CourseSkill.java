package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity: which skills a course covers, and how much it improves
 * that skill (used by the recommendation engine's "Expected Improvement" figure).
 */
@Entity
@Table(name = "course_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "skill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Estimated score points this course adds to the skill upon completion (e.g. +30)
    @Builder.Default
    private Double expectedImprovement = 20.0;
}