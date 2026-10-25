package com.capacityconnect.repository;

import com.capacityconnect.entity.CourseSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSkillRepository extends JpaRepository<CourseSkill, Long> {

    List<CourseSkill> findByCourseId(Long courseId);

    // Powers the recommendation engine (section 10): "which courses cover
    // this skill I'm gapping on?"
    List<CourseSkill> findBySkillId(Long skillId);

    // Efficient batch lookup: which courses cover ANY of these gap skills
    List<CourseSkill> findBySkillIdIn(List<Long> skillIds);
}