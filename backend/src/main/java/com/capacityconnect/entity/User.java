package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Core authentication entity. Every Trainee, Trainer, and Admin
 * has exactly one User record holding their login credentials and role.
 *
 * Profile-specific data (qualifications, skills, expertise, etc.)
 * lives in TraineeProfile / TrainerProfile, linked one-to-one to this entity.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt-hashed password - never store or return plain text
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Admin approval workflow: new signups start as PENDING for TRAINER/ADMIN
    // if you want approval gating; TRAINEE can be auto-approved.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

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

    public enum AccountStatus {
        PENDING,
        ACTIVE,
        REJECTED,
        SUSPENDED
    }
}