package com.capacityconnect.repository;

import com.capacityconnect.entity.TraineeAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraineeAchievementRepository extends JpaRepository<TraineeAchievement, Long> {

    List<TraineeAchievement> findByTraineeProfileId(Long traineeProfileId);

    boolean existsByTraineeProfileIdAndAchievementId(Long traineeProfileId, Long achievementId);
}