package com.organisation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.DocumentUploadTemp;

@Repository
public interface DocumentUploadTempRepository extends JpaRepository<DocumentUploadTemp, Long>{
	
	List<DocumentUploadTemp> findByOrgId(String orgId);
	
	Optional<DocumentUploadTemp> findByOrgIdAndDocType(String orgId, String docType);

}
