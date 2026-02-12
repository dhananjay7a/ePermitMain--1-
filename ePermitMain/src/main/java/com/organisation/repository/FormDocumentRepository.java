package com.organisation.repository;

import com.organisation.entity.FormDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FormDocument entity
 */
public interface FormDocumentRepository extends JpaRepository<FormDocument, Long> {

    /**
     * Find all form documents for a specific organization
     */
    List<FormDocument> findByOrgId(String orgId);

    /**
     * Find form documents by organization and form type
     */
    Optional<FormDocument> findByOrgIdAndFormType(String orgId, String formType);

    /**
     * Find all form documents of a specific type
     */
    List<FormDocument> findByFormType(String formType);

    Optional<FormDocument> findByOrgIdAndFormTypeAndFormStatus(String orgId, String string, String string2);

}
