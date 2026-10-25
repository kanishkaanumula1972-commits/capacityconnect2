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
 * Extended profile for a user with role = TRAINER.
 * One-to-one with User.
 */
@Entity
@Table(name = "trainer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    private String location;

    private String profilePhotoUrl;

    @Column(length = 1000)
    private String bio;

    // Years of overall training/industry experience
    private Integer yearsOfExperience;

    // Subjects/topics this trainer teaches, e.g. "Java", "Cloud Computing"
    @ElementCollection
    @CollectionTable(name = "trainer_subjects", joinColumns = @JoinColumn(name = "trainer_profile_id"))
    @Column(name = "subject")
    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Qualification> qualifications = new ArrayList<>();

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainerExpertise> expertiseList = new ArrayList<>();

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainerCertification> certifications = new ArrayList<>();

    // Denormalized average rating, recalculated whenever new feedback comes in.
    // Kept here (rather than always computing live) so trainer-matching queries are fast.
    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer totalRatings = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}