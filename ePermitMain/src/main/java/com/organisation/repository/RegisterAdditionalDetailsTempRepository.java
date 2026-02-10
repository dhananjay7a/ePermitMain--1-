package com.organisation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.RegisterAdditionalDetailsTemp;

@Repository
public interface RegisterAdditionalDetailsTempRepository extends JpaRepository<RegisterAdditionalDetailsTemp, String> {
	RegisterAdditionalDetailsTemp findByOrgId(String orgId);

}
