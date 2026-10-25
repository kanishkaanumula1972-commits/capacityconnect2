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
 * Extended profile for a user with role = TRAINEE.
 * One-to-one with User.
 */
@Entity
@Table(name = "trainee_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    private String location;

    // Stored file path/URL to the uploaded profile photo
    private String profilePhotoUrl;

    @Column(length = 1000)
    private String bio;

    @OneToMany(mappedBy = "traineeProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Qualification> qualifications = new ArrayList<>();

    @OneToMany(mappedBy = "traineeProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    // Interests are simple strings (e.g. "Java", "Cloud", "AI") - kept as an
    // element collection since they don't need their own rich entity/table with relations.
    @ElementCollection
    @CollectionTable(name = "trainee_interests", joinColumns = @JoinColumn(name = "trainee_profile_id"))
    @Column(name = "interest")
    @Builder.Default
    private List<String> interests = new ArrayList<>();

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