package com.organisation.service;

import com.organisation.dto.ApproveRejectRegistrationDTO;
import com.organisation.dto.ApprovalResponseDTO;

/**
 * Service interface for registration approval/rejection workflow
 */
public interface ApprovalWorkflowService {

    /**
     * Process approval or rejection of a registration
     * 
     * @param token
     */
    ApprovalResponseDTO approveRejectRegistration(String token, ApproveRejectRegistrationDTO request);

    /**
     * Get approval history for a specific organization
     */
    Object getApprovalHistory(String orgId);

}
