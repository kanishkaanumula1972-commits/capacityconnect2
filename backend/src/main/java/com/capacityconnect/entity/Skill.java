package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master catalog of all skills recognized by the platform.
 * Managed by Admin. Referenced by Trainees (TraineeSkill), Competencies
 * (CompetencySkill), and Courses (CourseSkill).
 *
 * Example: "Java", "Spring Boot", "REST API", "SQL", "Cloud", "Git"
 */
@Entity
@Table(name = "skills", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // e.g. "Programming Language", "Framework", "Database", "Cloud", "Soft Skill"
    private String category;

    @Column(length = 500)
    private String description;
}