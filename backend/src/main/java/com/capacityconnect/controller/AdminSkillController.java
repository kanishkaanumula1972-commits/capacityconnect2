package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.entity.Skill;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.SkillRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminSkillController {

    private final SkillRepository skillRepository;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDto {
        private Long id;
        @NotBlank(message = "Skill name is required")
        private String name;
        private String category;
        private String description;
    }

    @GetMapping("/api/public/skills")
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        List<SkillDto> skills = skillRepository.findAll().stream()
                .map(s -> new SkillDto(s.getId(), s.getName(), s.getCategory(), s.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/api/admin/skills")
    public ResponseEntity<SkillDto> createSkill(@Valid @RequestBody SkillDto dto) {
        if (skillRepository.existsByName(dto.getName())) {
            throw new BadRequestException("A skill with this name already exists");
        }
        Skill skill = Skill.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
        skill = skillRepository.save(skill);
        return ResponseEntity.ok(new SkillDto(skill.getId(), skill.getName(), skill.getCategory(), skill.getDescription()));
    }

    @DeleteMapping("/api/admin/skills/{id}")
    public ResponseEntity<MessageResponse> deleteSkill(@PathVariable Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found");
        }
        skillRepository.deleteById(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Skill deleted").success(true).build());
    }
}