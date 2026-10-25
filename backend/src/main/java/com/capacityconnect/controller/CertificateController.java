package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.profile.CertificateDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/trainees/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getMyCertificates(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(certificateService.getMyCertificates(principal.getId()));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CertificateDto> uploadCertificate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String name,
            @RequestParam String issuingOrganization,
            @RequestParam(required = false) String issueDate,
            @RequestParam(required = false) String credentialId,
            @RequestParam(required = false) List<Long> relatedSkillIds,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(certificateService.uploadCertificate(
                principal.getId(), name, issuingOrganization, issueDate, credentialId, relatedSkillIds, file
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCertificate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        certificateService.deleteCertificate(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Certificate removed").success(true).build());
    }
}