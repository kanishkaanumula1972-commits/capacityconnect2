package com.capacityconnect.repository;

import com.capacityconnect.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStatus(Course.CourseStatus status);

    List<Course> findByTrainerId(Long trainerProfileId);

    List<Course> findByCategory(String category);

    List<Course> findByDifficulty(Course.Difficulty difficulty);

    long countByStatus(Course.CourseStatus status);
}