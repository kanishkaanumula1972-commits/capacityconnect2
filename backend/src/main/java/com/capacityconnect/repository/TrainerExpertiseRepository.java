package com.capacityconnect.repository;

import com.capacityconnect.entity.TrainerExpertise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerExpertiseRepository extends JpaRepository<TrainerExpertise, Long> {

    List<TrainerExpertise> findByTrainerProfileId(Long trainerProfileId);

    // The core query behind Intelligent Trainer Matching (section 11):
    // "which trainers have expertise in this gap skill?"
    List<TrainerExpertise> findBySkillId(Long skillId);

    Optional<TrainerExpertise> findByTrainerProfileIdAndSkillId(Long trainerProfileId, Long skillId);
}