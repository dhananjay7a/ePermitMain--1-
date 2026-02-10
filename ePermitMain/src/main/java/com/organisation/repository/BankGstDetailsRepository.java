package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.BankGstDetails;

@Repository
public interface BankGstDetailsRepository extends JpaRepository<BankGstDetails, String> {
	BankGstDetails findByOrgId(String orgId);
}
