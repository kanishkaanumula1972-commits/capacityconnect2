package com.capacityconnect.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperienceDto {
    private Long id;

    @NotBlank(message = "Organization is required")
    private String organization;

    @NotBlank(message = "Role is required")
    private String role;

    private Double yearsOfExperience;

    private String responsibilities;
}