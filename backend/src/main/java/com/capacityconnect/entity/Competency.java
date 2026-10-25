package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A competency represents a target job-role or capability, defined by
 * Admin, made up of a set of required skills each with a required
 * proficiency threshold (see CompetencySkill).
 *
 * Example: "Backend Development" requires Java(Advanced), Spring Boot(Intermediate),
 * REST API(Intermediate), SQL(Intermediate), Git(Beginner).
 */
@Entity
@Table(name = "competencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    // e.g. "Software Development", "Data & Analytics", "Cloud & DevOps"
    private String category;

    @OneToMany(mappedBy = "competency", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompetencySkill> requiredSkills = new ArrayList<>();
}