package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A platform-wide announcement published by Admin (section 22),
 * shown on the homepage/dashboard to all users.
 */
@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AnnouncementType type = AnnouncementType.GENERAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by_user_id", nullable = false)
    private User publishedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AnnouncementType {
        GENERAL,
        NEW_COURSE,
        ACHIEVEMENT,
        TRAINING_PROGRAM,
        NEW_CONTENT
    }
}