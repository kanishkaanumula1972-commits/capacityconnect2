package com.capacityconnect.repository;

import com.capacityconnect.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    Optional<TrainerProfile> findByUserId(Long userId);

    Optional<TrainerProfile> findByUserEmail(String email);

    long countBy();
}