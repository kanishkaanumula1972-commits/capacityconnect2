package com.capacityconnect.repository;

import com.capacityconnect.entity.TraineeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeProfileRepository extends JpaRepository<TraineeProfile, Long> {

    Optional<TraineeProfile> findByUserId(Long userId);

    Optional<TraineeProfile> findByUserEmail(String email);

    long countBy();
}