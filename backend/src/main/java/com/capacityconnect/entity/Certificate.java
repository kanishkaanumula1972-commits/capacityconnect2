package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A certificate uploaded/earned by a trainee (section 6).
 * Linked to the skills it validates - used as supporting evidence in the
 * "Evidence-Based Competency Profile" (section 31, innovation #4).
 */
@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id", nullable = false)
    private TraineeProfile traineeProfile;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String issuingOrganization;

    private LocalDate issueDate;

    private String credentialId;

    // Path/URL to the uploaded certificate file (PDF/image)
    @Column(nullable = false)
    private String fileUrl;

    // Skills this certificate validates, e.g. "AWS Certified" -> ["Cloud", "AWS"]
    @ManyToMany
    @JoinTable(
            name = "certificate_related_skills",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private List<Skill> relatedSkills = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}