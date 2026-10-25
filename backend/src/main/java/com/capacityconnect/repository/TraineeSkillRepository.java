package com.capacityconnect.repository;

import com.capacityconnect.entity.TraineeSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TraineeSkillRepository extends JpaRepository<TraineeSkill, Long> {

    // Powers the trainee's own competency profile view (section 8)
    List<TraineeSkill> findByTraineeProfileId(Long traineeProfileId);

    Optional<TraineeSkill> findByTraineeProfileIdAndSkillId(Long traineeProfileId, Long skillId);

    // Powers the Organizational Competency Heatmap (section 18):
    // average score for a given skill across ALL trainees
    List<TraineeSkill> findBySkillId(Long skillId);
}