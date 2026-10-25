package com.capacityconnect.repository;

import com.capacityconnect.entity.TrainerCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerCertificationRepository extends JpaRepository<TrainerCertification, Long> {

    List<TrainerCertification> findByTrainerProfileId(Long trainerProfileId);

    long countByTrainerProfileId(Long trainerProfileId);
}