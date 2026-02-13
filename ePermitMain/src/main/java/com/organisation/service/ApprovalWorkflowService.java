package com.organisation.service;

import com.organisation.dto.ApproveRejectRegistrationDTO;

import org.springframework.web.multipart.MultipartFile;

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

    ApprovalResponseDTO approveRejectRegistrationWithFile(String token, ApproveRejectRegistrationDTO request,
            MultipartFile pdfFile);

    /**
     * Get approval history for a specific organization
     */
    Object getApprovalHistory(String orgId);

    byte[] getSignedPdf(String token, String orgId);

    byte[] getUnsignedPdf(String token, String orgId);

}
