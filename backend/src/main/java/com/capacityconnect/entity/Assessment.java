package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An MCQ assessment created by a Trainer, tied to a Course and/or a
 * specific Skill/Competency. Completing this with a good score is what
 * drives the "Assessment -> Competency Update" flow (section 16).
 */
@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // The primary skill this assessment measures/updates (e.g. "REST API").
    // A trainee's score on this assessment directly feeds into their
    // TraineeSkill.score for this skill.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill targetSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id")
    private Competency targetCompetency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_trainer_id", nullable = false)
    private TrainerProfile createdBy;

    // Total marks across all questions (denormalized for quick display)
    @Builder.Default
    private Integer totalMarks = 0;

    // Duration allowed to complete the assessment, in minutes
    private Integer durationMinutes;

    private LocalDateTime deadline;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}