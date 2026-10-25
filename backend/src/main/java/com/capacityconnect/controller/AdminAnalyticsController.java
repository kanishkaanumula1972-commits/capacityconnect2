package com.capacityconnect.controller;

import com.capacityconnect.dto.admin.OrganizationalHeatmapDto;
import com.capacityconnect.dto.admin.SkillGapAlertDto;
import com.capacityconnect.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    // Section 18: full heatmap - skill health + top skill gaps
    @GetMapping("/competency-heatmap")
    public ResponseEntity<OrganizationalHeatmapDto> getHeatmap(
            @RequestParam(defaultValue = "5") int topGapsLimit
    ) {
        return ResponseEntity.ok(adminAnalyticsService.getHeatmap(topGapsLimit));
    }

    // Section 19: drill-down when Admin clicks a specific gap
    // (e.g. "Cloud Computing -> 126 trainees affected")
    @GetMapping("/competency-gaps/{skillId}")
    public ResponseEntity<SkillGapAlertDto> getSkillGapDetail(@PathVariable Long skillId) {
        return ResponseEntity.ok(adminAnalyticsService.getSkillGapDetail(skillId));
    }
}