package com.capacityconnect.repository;

import com.capacityconnect.entity.CompetencySkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetencySkillRepository extends JpaRepository<CompetencySkill, Long> {

    // The required-skill breakdown for a given competency (section 7)
    // - this is one side of every gap-detection comparison
    List<CompetencySkill> findByCompetencyId(Long competencyId);

    // Used to find which competencies require a given skill
    // (e.g. "what competencies need REST API?")
    List<CompetencySkill> findBySkillId(Long skillId);
}