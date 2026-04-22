package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.TermsAndConditions;

@Repository
public interface TermsAndConditionsRepository extends JpaRepository<TermsAndConditions, Long>{
	
	int countByFormTypeAndIsMandatoryTrue(String formType);
	
    List<TermsAndConditions> findByFormType(String formType);
}
