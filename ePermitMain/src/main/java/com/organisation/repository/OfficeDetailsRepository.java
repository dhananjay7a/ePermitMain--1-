package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.OrgOfficeDetails;

@Repository
public interface OfficeDetailsRepository extends JpaRepository<OrgOfficeDetails, Long>{
	Optional<OrgOfficeDetails> findByDistrictCodeAndOrgCategory(String districtCode, String orgCategory);
	Optional<OrgOfficeDetails> findByOrgOfficeCode(String orgOfficeCode);
	
}
