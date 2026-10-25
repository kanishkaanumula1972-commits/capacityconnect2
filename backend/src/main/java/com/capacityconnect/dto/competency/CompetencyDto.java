package com.capacityconnect.dto.competency;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Full competency definition, managed by Admin (section 7).
 * Example: "Backend Development" requiring Java(80), Spring Boot(70), etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetencyDto {
    private Long id;

    @NotBlank(message = "Competency name is required")
    private String name;

    private String description;

    private String category;

    @Builder.Default
    private List<CompetencySkillDto> requiredSkills = new ArrayList<>();
}