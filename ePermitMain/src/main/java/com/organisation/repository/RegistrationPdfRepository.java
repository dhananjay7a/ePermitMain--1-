package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.RegistrationPdf;
@Repository
public interface RegistrationPdfRepository extends JpaRepository<RegistrationPdf, Long> {

	Optional<RegistrationPdf> findByOrgId(String orgId);
}