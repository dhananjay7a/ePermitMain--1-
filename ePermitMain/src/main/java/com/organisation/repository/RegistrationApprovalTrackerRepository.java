package com.organisation.repository;

import com.organisation.entity.RegistrationApprovalTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RegistrationApprovalTracker entity
 */
@Repository
public interface RegistrationApprovalTrackerRepository extends JpaRepository<RegistrationApprovalTracker, Long> {

    /**
     * Find all approval records for a specific organization
     */
    List<RegistrationApprovalTracker> findByOrgIdOrderByCreatedOnDesc(String orgId);

    /**
     * Find latest approval record for an organization
     */
    Optional<RegistrationApprovalTracker> findFirstByOrgIdOrderByCreatedOnDesc(String orgId);

    /**
     * Find approval records by approver type
     */
    List<RegistrationApprovalTracker> findByApproverType(String approverType);

    /**
     * Find approval records by action (APPROVE, REJECT)
     */
    List<RegistrationApprovalTracker> findByApproverAction(String approverAction);

}
