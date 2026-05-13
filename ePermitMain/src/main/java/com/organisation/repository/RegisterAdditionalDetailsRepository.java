package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.RegisterAdditionalDetails;
import com.organisation.model.RegistrationMstr;

@Repository
public interface RegisterAdditionalDetailsRepository extends JpaRepository<RegisterAdditionalDetails, String> {
	Optional<RegisterAdditionalDetails> findByOrgId(String orgId);
}

