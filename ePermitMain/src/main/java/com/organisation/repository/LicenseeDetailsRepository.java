package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.LicenseeDetailsTemp;

@Repository
public interface LicenseeDetailsRepository extends JpaRepository<LicenseeDetailsTemp, String>{
	LicenseeDetailsTemp findByOrgId(String orgId);

}
