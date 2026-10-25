package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.competency.CompetencyDto;
import com.capacityconnect.service.CompetencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin management of Competency definitions (section 7 & 24).
 * Also exposed for read-only access by Trainees/Trainers via a
 * separate public listing endpoint (see getAllCompetenciesPublic below)
 * since trainees need to pick a target competency/goal.
 */
@RestController
@RequiredArgsConstructor
public class AdminCompetencyController {

    private final CompetencyService competencyService;

    // ---------- Admin-only management ----------

    @GetMapping("/api/admin/competencies")
    public ResponseEntity<List<CompetencyDto>> getAllCompetencies() {
        return ResponseEntity.ok(competencyService.getAllCompetencies());
    }

    @PostMapping("/api/admin/competencies")
    public ResponseEntity<CompetencyDto> createCompetency(@Valid @RequestBody CompetencyDto dto) {
        return ResponseEntity.ok(competencyService.createCompetency(dto));
    }

    @PutMapping("/api/admin/competencies/{id}")
    public ResponseEntity<CompetencyDto> updateCompetency(
            @PathVariable Long id,
            @Valid @RequestBody CompetencyDto dto
    ) {
        return ResponseEntity.ok(competencyService.updateCompetency(id, dto));
    }

    @DeleteMapping("/api/admin/competencies/{id}")
    public ResponseEntity<MessageResponse> deleteCompetency(@PathVariable Long id) {
        competencyService.deleteCompetency(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Competency deleted").success(true).build());
    }

    // ---------- Read-only, any authenticated user ----------
    // Needed so Trainees can browse/pick a target competency to set as their goal

    @GetMapping("/api/public/competencies")
    public ResponseEntity<List<CompetencyDto>> getAllCompetenciesPublic() {
        return ResponseEntity.ok(competencyService.getAllCompetencies());
    }

    @GetMapping("/api/public/competencies/{id}")
    public ResponseEntity<CompetencyDto> getCompetencyPublic(@PathVariable Long id) {
        return ResponseEntity.ok(competencyService.getCompetencyById(id));
    }
}