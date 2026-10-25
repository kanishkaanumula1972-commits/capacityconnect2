package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A professional certification held by a trainer - contributes to the
 * trainer matching score (section 11: "Certification 15%").
 */
@Entity
@Table(name = "trainer_certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_profile_id", nullable = false)
    private TrainerProfile trainerProfile;

    @Column(nullable = false)
    private String name;

    private String issuingOrganization;

    private LocalDate issueDate;

    private String credentialId;
}