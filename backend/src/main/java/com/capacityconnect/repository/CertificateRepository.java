package com.capacityconnect.repository;

import com.capacityconnect.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByTraineeProfileId(Long traineeProfileId);

    long countByTraineeProfileId(Long traineeProfileId);
}