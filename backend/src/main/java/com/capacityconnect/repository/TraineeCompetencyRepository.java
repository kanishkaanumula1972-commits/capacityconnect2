package com.capacityconnect.repository;

import com.capacityconnect.entity.TraineeCompetency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TraineeCompetencyRepository extends JpaRepository<TraineeCompetency, Long> {

    List<TraineeCompetency> findByTraineeProfileId(Long traineeProfileId);

    Optional<TraineeCompetency> findByTraineeProfileIdAndCompetencyId(Long traineeProfileId, Long competencyId);

    // The trainee's currently active goal competency (e.g. "Backend Developer")
    // used in the demo scenario's "Your Goal: Backend Developer" screen (section 29)
    Optional<TraineeCompetency> findByTraineeProfileIdAndIsTargetGoalTrue(Long traineeProfileId);
}