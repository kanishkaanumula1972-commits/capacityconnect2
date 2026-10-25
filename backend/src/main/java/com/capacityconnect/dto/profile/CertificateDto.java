package com.capacityconnect.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateDto {
    private Long id;

    @NotBlank(message = "Certificate name is required")
    private String name;

    @NotBlank(message = "Issuing organization is required")
    private String issuingOrganization;

    private LocalDate issueDate;

    private String credentialId;

    // Output only - set by the server after file upload
    private String fileUrl;

    @Builder.Default
    private List<Long> relatedSkillIds = new ArrayList<>();

    // Output only - skill names for display
    @Builder.Default
    private List<String> relatedSkillNames = new ArrayList<>();
}