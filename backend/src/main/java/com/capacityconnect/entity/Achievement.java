package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A badge/achievement definition (section 23) - e.g. "Java Mastery",
 * "Assessment Champion". Managed by Admin; awarded to trainees via
 * TraineeAchievement.
 */
@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    // Emoji or icon identifier shown in the UI, e.g. "🏆", "🎯", "📚", "🚀"
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCriteria criteria;

    // Threshold value tied to the criteria, e.g. 90 (for score-based),
    // or 1 (for course-completion-count-based)
    private Double criteriaThreshold;

    public enum AchievementCriteria {
        SKILL_MASTERY,          // reach a certain score in a specific skill
        ASSESSMENT_HIGH_SCORE,  // score above threshold in an assessment
        COURSE_COMPLETION_COUNT,// complete N courses
        SKILL_IMPROVEMENT       // improve a skill score by threshold amount
    }
}