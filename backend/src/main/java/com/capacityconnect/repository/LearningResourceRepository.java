package com.capacityconnect.repository;

import com.capacityconnect.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findByCourseId(Long courseId);

    // Powers the Trainer Knowledge Library filters (section 14)
    List<LearningResource> findByTopic(String topic);

    List<LearningResource> findByDifficulty(com.capacityconnect.entity.Course.Difficulty difficulty);

    List<LearningResource> findByType(LearningResource.ResourceType type);
}