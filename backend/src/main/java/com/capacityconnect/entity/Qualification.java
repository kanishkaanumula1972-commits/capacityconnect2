package com.capacityconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single qualification/degree entry, owned by either a TraineeProfile
 * or a TrainerProfile (exactly one of the two will be set).
 */
@Entity
@Table(name = "qualifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String institution;

    private Integer year;

    private String specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_profile_id")
    private TraineeProfile traineeProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_profile_id")
    private TrainerProfile trainerProfile;
}