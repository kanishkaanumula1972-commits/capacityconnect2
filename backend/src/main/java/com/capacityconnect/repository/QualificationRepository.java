package com.capacityconnect.repository;

import com.capacityconnect.entity.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {

    List<Qualification> findByTraineeProfileId(Long traineeProfileId);

    List<Qualification> findByTrainerProfileId(Long trainerProfileId);
}