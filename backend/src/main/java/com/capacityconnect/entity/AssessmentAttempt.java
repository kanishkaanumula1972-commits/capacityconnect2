package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Records a trainee's completed attempt at an Assessment: their score,
 * and the before/after competency snapshot this attempt triggered.
 * This is the audit trail behind the "Competency Improved" screen (section 16).
 */
@Entity
@Table(name = "assessment_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    // Answers submitted, stored as JSON-ish "questionId:selectedOption" pairs
    // e.g. "1:A,2:C,3:B" - kept simple for hackathon scope rather than a
    // separate AnswerDetail entity per question.
    @Column(length = 2000)
    private String submittedAnswers;

    @Column(nullable = false)
    private Integer marksObtained;

    @Column(nullable = false)
    private Integer totalMarks;

    // Percentage score, e.g. 88.0
    @Column(nullable = false)
    private Double percentageScore;

    // Skill score snapshot BEFORE this attempt (e.g. 31.0 for REST API)
    private Double skillScoreBefore;

    // Skill score snapshot AFTER this attempt was applied (e.g. 72.0)
    private Double skillScoreAfter;

    @Column(nullable = false)
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        attemptedAt = LocalDateTime.now();
    }
}