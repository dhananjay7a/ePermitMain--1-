package com.organisation.approvalworkflow.service.impl;

import com.organisation.approvalworkflow.dto.ApproveRejectRegistrationDTO;
import com.organisation.approvalworkflow.dto.ApprovalResponseDTO;
import com.organisation.approvalworkflow.entity.FormDocument;
import com.organisation.approvalworkflow.entity.RegistrationApprovalTracker;
import com.organisation.approvalworkflow.exception.ApprovalWorkflowException;
import com.organisation.approvalworkflow.exception.RegistrationNotFoundException;
import com.organisation.approvalworkflow.repository.FormDocumentRepository;
import com.organisation.approvalworkflow.repository.RegistrationApprovalTrackerRepository;
import com.organisation.approvalworkflow.service.ApprovalWorkflowService;
import com.organisation.approvalworkflow.service.PDFGenerationService;
import com.organisation.approvalworkflow.util.ApprovalValidationUtil;
import com.organisation.constants.OrgConstants.REGISTRATION_STATUS;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.UserMapping;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.RegistrationStatusRepository;
import com.organisation.repository.UserMappingRepository;
import com.organisation.security.TokenService;
import com.register.model.RegistrationStatus;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for registration approval/rejection workflow
 * Handles the complete approval process including validations, status updates,
 * tracker entries, and user-market mapping
 */
@Service
@Slf4j
public class ApprovalWorkflowServiceImpl implements ApprovalWorkflowService {

    @Autowired
    private RegistrationMstrRepository registrationRepository;

    @Autowired
    private RegistrationApprovalTrackerRepository approvalTrackerRepository;

    @Autowired
    private UserMappingRepository userMappingRepository;

    @Autowired
    private FormDocumentRepository formDocumentRepository;

    @Autowired
    private RegistrationStatusRepository registrationStatusRepository;

    @Autowired
    private ApprovalValidationUtil validationUtil;

    @Autowired
    private PDFGenerationService pdfGenerationService;

    @Autowired
    private TokenService ts;

    /**
     * Process approval or rejection of registration with complete transaction
     * handling
     */
    @Override
    @Transactional
    public ApprovalResponseDTO approveRejectRegistration(String token, ApproveRejectRegistrationDTO request) {

        String generatedFilePath = null;

        try {
            log.info("Processing approval/rejection for OrgId: {}, Action: {}, ApproverType: {}",
                    request.getOrgId(), request.getRegStatus(), request.getApproverType());

            // ---------- TOKEN ----------
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = ts.validateToken(token);
            if (claims == null) {
                throw new RuntimeException("Invalid Token");
            }

            String userId = ts.extractUserId(token);

            // ---------- ORG FROM REQUEST ----------
            String orgId = request.getOrgId();
            if (orgId == null || orgId.isBlank()) {
                throw new RuntimeException("orgId is required");
            }

            request.setApproverUserId(userId);

            // Step 1: Fetch and validate registration
            RegistrationMstr registration = registrationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new RegistrationNotFoundException(request.getOrgId()));

            // Step 2: Fetch current status from RegistrationStatus table
            Optional<RegistrationStatus> currentStatusOpt = registrationStatusRepository
                    .findTopByOrgIdOrderByActionDateTimeDesc(request.getOrgId());
            String previousStatus = currentStatusOpt.isPresent() ? currentStatusOpt.get().getApplicationStatus()
                    : REGISTRATION_STATUS.PENDING; // Default to PENDING if no status record exists

            // Step 3: Validate status transition based on approver type
            validationUtil.validateStatusTransition(previousStatus, request.getApproverType());

            // Step 4: Determine action and new status
            boolean isApprove = "a".equalsIgnoreCase(request.getRegStatus());
            String newStatus = isApprove ? getApprovedStatus(request.getApproverType()) : REGISTRATION_STATUS.REJECTED;

            // Step 5: Handle approval-specific logic
            if (isApprove) {
                generatedFilePath = handleApproval(registration, request, newStatus);
            } else {
                handleRejection(registration, request, newStatus);
            }

            // Step 6: Create approval tracker record
            createApprovalTracker(registration, request, previousStatus, newStatus);

            // Step 7: Save updated registration (only non-status fields)
            registrationRepository.save(registration);

            log.info("Successfully processed approval/rejection for OrgId: {}, NewStatus: {}",
                    request.getOrgId(), newStatus);

            return ApprovalResponseDTO.builder()
                    .errorCode(0)
                    .status("SUCCESS")
                    .filePath(generatedFilePath)
                    .newStatus(newStatus)
                    .message("Registration " + (isApprove ? "approved" : "rejected") + " successfully")
                    .build();

        } catch (RegistrationNotFoundException e) {
            log.error("Registration not found: {}", request.getOrgId(), e);
            return buildErrorResponse(e.getErrorCode(), e.getMessage(), "VALIDATION_FAILED", request.getOrgId());
        } catch (ApprovalWorkflowException e) {
            log.error("Approval workflow error: {}", e.getErrorCode(), e);
            return buildErrorResponse(e.getErrorCode(), e.getMessage(), "VALIDATION_FAILED", request.getOrgId());
        } catch (Exception e) {
            log.error("Unexpected error during approval processing", e);
            return buildErrorResponse("INTERNAL_ERROR", "An unexpected error occurred: " + e.getMessage(), "FAILED",
                    request.getOrgId());
        }
    }

    /**
     * Handle approval logic including status update, market mapping, and form
     * generation
     */
    private String handleApproval(RegistrationMstr registration, ApproveRejectRegistrationDTO request,
            String newStatus) {

        // Update resolution number if provided
        /*
         * if (request.getResNo() != null && !request.getResNo().isEmpty()) {
         * registration.setResolutionNo(request.getResNo());
         * }
         */

        // Update fee validity if required and provided
        if ("FINAL_APPROVER".equalsIgnoreCase(request.getApproverType()) &&
                !registration.isLicenseExists() && request.getRegFeeValidity() != null) {
            validationUtil.validateFeeValidity(request.getRegFeeValidity(), request.getOrgId());
            registration.setRegFeeValidity(java.sql.Timestamp.valueOf(
                    request.getRegFeeValidity().atStartOfDay()));
        }

        // Step 1: Create registration status record
        createRegistrationStatus(registration, newStatus, request);

        // Step 2: Create user-market mapping for approved organizations
        createUserMarketMapping(registration);

        // Step 3: Generate forms four

        return generateFormFour(registration);

    }

    /**
     * Handle rejection logic
     */
    private void handleRejection(RegistrationMstr registration, ApproveRejectRegistrationDTO request,
            String newStatus) {
        // Create registration status record for rejection
        createRegistrationStatus(registration, newStatus, request);

        // Store rejection remarks if provided
        if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
            log.info("Rejection remarks for {}: {}", registration.getOrgId(), request.getRemarks());
        }
    }

    /**
     * Create or update registration status record
     */
    private void createRegistrationStatus(RegistrationMstr registration, String newStatus,
            ApproveRejectRegistrationDTO request) {
        try {
            RegistrationStatus registrationStatus = new RegistrationStatus();
            registrationStatus.setOrgId(registration.getOrgId());
            registrationStatus.setApplicationStatus(newStatus);
            registrationStatus.setActionDateTime(LocalDateTime.now());
            registrationStatus.setActionTakenBy(request.getApproverUserId());
            registrationStatus.setRemarks(request.getRemarks() != null ? request.getRemarks() : "");

            registrationStatusRepository.save(registrationStatus);
            log.info("Registration status created for OrgId: {} with status: {}",
                    registration.getOrgId(), newStatus);

        } catch (Exception e) {
            log.error("Error creating registration status for OrgId: {}", registration.getOrgId(), e);
            throw new ApprovalWorkflowException("STATUS_UPDATE_ERROR",
                    "Failed to create registration status: " + e.getMessage());
        }
    }

    /**
     * Create user-market mapping for approved registrations
     */
    private void createUserMarketMapping(RegistrationMstr registration) {
        try {
            // Check if mapping already exists
            String userId = "U_" + registration.getOrgId();

            UserMapping existingMapping = userMappingRepository.findByUserId(userId);
            if (existingMapping != null) {
                log.info("User mapping already exists for userId: {}", userId);
                return;
            }

            UserMapping userMapping = new UserMapping();
            userMapping.setUserId(userId);
            userMapping.setMarketCode(registration.getOrgBaseMarket());
            userMapping.setOrgId(registration.getOrgId());
            userMapping.setIsActive("Y");
            userMapping.setCreatedOn(new java.sql.Timestamp(System.currentTimeMillis()));

            userMappingRepository.save(userMapping);
            log.info("User-market mapping created for userId: {}, marketCode: {}",
                    userId, registration.getOrgBaseMarket());

        } catch (Exception e) {
            log.error("Error creating user-market mapping for OrgId: {}", registration.getOrgId(), e);
            throw new ApprovalWorkflowException("USER_MAPPING_ERROR",
                    "Failed to create user-market mapping: " + e.getMessage());
        }
    }

    /**
     * Generate Form 4 for applicable organizations using iText
     */
    private String generateFormFour(RegistrationMstr registration) {
        try {
            log.info("Generating Form 4 for OrgId: {}", registration.getOrgId());

            String filePath = pdfGenerationService.generateFormFour(registration);

            if (filePath != null && !filePath.isEmpty()) {
                FormDocument formDoc = FormDocument.builder()
                        .orgId(registration.getOrgId())
                        .formType("FORM_FOUR")
                        .fileName("Form4_" + registration.getOrgId() + ".pdf")
                        .filePath(filePath)
                        .createdBy("SYSTEM")
                        .build();

                formDocumentRepository.save(formDoc);
                log.info("Form 4 generated and saved for OrgId: {}", registration.getOrgId());
                return filePath;
            }
            return null;

        } catch (Exception e) {
            log.error("Error generating Form 4 for OrgId: {}", registration.getOrgId(), e);
            throw new ApprovalWorkflowException("FORM4_GENERATION_ERROR",
                    "Failed to generate Form 4: " + e.getMessage());
        }
    }

    /**
     * Create approval tracker record for audit trail
     */
    private void createApprovalTracker(RegistrationMstr registration,
            ApproveRejectRegistrationDTO request,
            String previousStatus, String newStatus) {

        try {
            RegistrationApprovalTracker tracker = RegistrationApprovalTracker.builder()
                    .orgId(registration.getOrgId())
                    .orgName(registration.getOrgName())
                    .orgCategory(registration.getOrgCategory())
                    .orgRegistrationStatus(
                            newStatus.equals(REGISTRATION_STATUS.DIGITAL_SIGNED) ? REGISTRATION_STATUS.FINAL_APPROVED
                                    : REGISTRATION_STATUS.PENDING)
                    .approverType(request.getApproverType())
                    .approverAction("a".equalsIgnoreCase(request.getRegStatus()) ? "APPROVE" : "REJECT")
                    .approverUserId(request.getApproverUserId())
                    .remarks(request.getRemarks())
                    .newStatus(newStatus)
                    .createdBy(request.getApproverUserId())
                    .build();

            approvalTrackerRepository.save(tracker);
            log.info("Approval tracker created for OrgId: {} with action: {}",
                    registration.getOrgId(), tracker.getApproverAction());

        } catch (Exception e) {
            log.error("Error creating approval tracker for OrgId: {}", registration.getOrgId(), e);
            throw new ApprovalWorkflowException("TRACKER_ERROR",
                    "Failed to create approval tracker: " + e.getMessage());
        }
    }

    /**
     * Determine the next status based on approver type
     */
    private String getApprovedStatus(String approverType) {
        if ("SCRUTINY".equalsIgnoreCase(approverType)) {
            return REGISTRATION_STATUS.SCRUTINY;
        } else if ("FINAL_APPROVER".equalsIgnoreCase(approverType)) {
            return REGISTRATION_STATUS.DSIGN_PENDING;
        } else if ("DIGITAL_APPROVER".equalsIgnoreCase(approverType)) {
            return REGISTRATION_STATUS.DIGITAL_APPROVED;
        } else if ("DIGITAL_SIGN".equalsIgnoreCase(approverType)) {
            return REGISTRATION_STATUS.DIGITAL_SIGNED;
        }
        return REGISTRATION_STATUS.DSIGN_PENDING;
    }

    /**
     * Build error response DTO
     */
    private ApprovalResponseDTO buildErrorResponse(String errorCode, String message,
            String status, String orgId) {
        return ApprovalResponseDTO.builder()
                .errorCode(errorCode.hashCode())
                .status(status)
                .orgId(orgId)
                .message(message)
                .build();
    }

    /**
     * Get approval history for an organization
     */
    @Override
    public Object getApprovalHistory(String orgId) {
        try {
            List<RegistrationApprovalTracker> history = approvalTrackerRepository
                    .findByOrgIdOrderByCreatedOnDesc(orgId);

            if (history.isEmpty()) {
                return ApprovalResponseDTO.builder()
                        .status("NO_HISTORY")
                        .orgId(orgId)
                        .message("No approval history found for this organization")
                        .build();
            }

            return history;

        } catch (Exception e) {
            log.error("Error retrieving approval history for OrgId: {}", orgId, e);
            throw new ApprovalWorkflowException("HISTORY_RETRIEVAL_ERROR",
                    "Failed to retrieve approval history: " + e.getMessage());
        }
    }

}
