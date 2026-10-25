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
public class QualificationDto {
    private Long id;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Institution is required")
    private String institution;

    private Integer year;

    private String specialization;
}