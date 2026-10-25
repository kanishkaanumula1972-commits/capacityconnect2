package com.capacityconnect.repository;

import com.capacityconnect.entity.CourseCompetency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseCompetencyRepository extends JpaRepository<CourseCompetency, Long> {

    List<CourseCompetency> findByCourseId(Long courseId);

    // Powers "Related Courses" on a Competency (section 7)
    List<CourseCompetency> findByCompetencyId(Long competencyId);
}