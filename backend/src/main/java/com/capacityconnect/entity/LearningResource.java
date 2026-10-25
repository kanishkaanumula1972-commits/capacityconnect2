package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A single learning resource attached to a course - a recorded lecture,
 * PDF, PPT, document, or other study material. Also powers the standalone
 * "Trainer Knowledge Library" (section 14), which is just a filtered view
 * across all resources regardless of course.
 */
@Entity
@Table(name = "learning_resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    // Path/URL to the stored file (video, PDF, PPT, etc.)
    @Column(nullable = false)
    private String fileUrl;

    // Topic/subject tag for library filtering (section 14)
    private String topic;

    @Enumerated(EnumType.STRING)
    private Course.Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public enum ResourceType {
        RECORDED_LECTURE,
        VIDEO,
        PDF,
        PPT,
        DOCUMENT,
        ASSIGNMENT
    }
}