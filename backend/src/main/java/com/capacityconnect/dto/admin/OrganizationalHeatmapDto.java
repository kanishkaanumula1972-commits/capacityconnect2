package com.capacityconnect.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationalHeatmapDto {
    @Builder.Default
    private List<OrgSkillHealthDto> skillHealth = new ArrayList<>();

    @Builder.Default
    private List<SkillGapAlertDto> topSkillGaps = new ArrayList<>();
}