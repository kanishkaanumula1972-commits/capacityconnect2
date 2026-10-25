package com.capacityconnect.repository;

import com.capacityconnect.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseId(Long courseId);

    List<Assessment> findByCreatedById(Long trainerProfileId);

    // Powers "Improve This Skill -> take this assessment" flow (section 9/10)
    List<Assessment> findByTargetSkillId(Long skillId);

    List<Assessment> findByTargetCompetencyId(Long competencyId);
}