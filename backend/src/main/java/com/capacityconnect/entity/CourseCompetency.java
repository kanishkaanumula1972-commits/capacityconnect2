package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity: which broader competencies a course contributes toward.
 * Used so a competency's "Related Courses" (section 7) can be looked up directly.
 */
@Entity
@Table(name = "course_competencies", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "competency_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private Competency competency;
}