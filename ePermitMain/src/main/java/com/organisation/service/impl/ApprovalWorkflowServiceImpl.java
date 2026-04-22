package com.organisation.service.impl;

import com.organisation.dto.ApproveRejectRegistrationDTO;
import com.organisation.dto.ApprovalResponseDTO;
import com.organisation.entity.FormDocument;
import com.organisation.entity.RegistrationApprovalTracker;
import com.organisation.exception.ApprovalWorkflowException;
import com.organisation.exception.RegistrationNotFoundException;
import com.organisation.repository.FormDocumentRepository;
import com.organisation.repository.RegistrationApprovalTrackerRepository;
import com.organisation.service.ApprovalWorkflowService;
import com.organisation.service.PDFGenerationService;
import com.organisation.util.ApprovalValidationUtil;
import com.organisation.constants.OrgConstants.REGISTRATION_STATUS;
import com.organisation.model.RegistrationMstr;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.RegistrationStatusRepository;
import com.organisation.security.TokenService;
import com.register.model.RegistrationStatus;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
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

    /*
     * @Autowired
     * private UserMappingRepository userMappingRepository;
     */

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

    @Value("${file.signed.pdf.dir:./signed-pdf/}")
    private String signedPdfStoragePath;

    private static final String FORM_FOUR_FILENAME = "Form4.pdf";
    private static final String FORM_FOUR_FOLDER_NAME = "form4";

    /**
     * Process approval or rejection of registration with complete transaction
     * handling
     */
    @Override
    @Transactional
    public ApprovalResponseDTO approveRejectRegistration(String token, ApproveRejectRegistrationDTO request) {

        String generatedFilePath = "";

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
            if (request.getRegStatus() == null || request.getApproverType() == null || request.getRemarks() == null) {
                throw new RuntimeException("regStatus, approverType and remarks are required");
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
            if (isApprove && request.getApproverType().equalsIgnoreCase("SCRUNITY")) {
                handleApproval(registration, request, newStatus);
            } else if (isApprove && request.getApproverType().equalsIgnoreCase("FINAL_APPROVER")) {
                handleApproval(registration, request, newStatus);
                generatedFilePath = generateFormFour(registration);
            } else if (isApprove && request.getApproverType().equalsIgnoreCase("DIGITAL_APPROVER")) {
                handleApproval(registration, request, newStatus);
                // registration.setRegFeeValidity(Timestamp.valueOf(request.getRegFeeValidity().atStartOfDay()));
                // createUserMarketMapping(registration);
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
            return buildErrorResponse("-1", "An unexpected error occurred: " + e.getMessage(), "FAILED",
                    request.getOrgId());
        }
    }

    // uplaod signed pdf and save path in db
    @Override
    public ApprovalResponseDTO approveRejectRegistrationWithFile(String token, ApproveRejectRegistrationDTO request,
            MultipartFile pdfFile) {
        try {
            // Validate file
            if (pdfFile == null || pdfFile.isEmpty() || !pdfFile.getContentType().equalsIgnoreCase("application/pdf")) {
                throw new RuntimeException("PDF file is required for approval.");
            }

            String orgId = request.getOrgId();

            // Create directory
            String path = signedPdfStoragePath + "/" + orgId + "/" + FORM_FOUR_FOLDER_NAME;
            Path filePath = Paths.get(path, FORM_FOUR_FILENAME);
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Process approval
            ApprovalResponseDTO response = approveRejectRegistration(token, request);

            if (response.getErrorCode() == 0 && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
                // Save file
                Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                formDocumentRepository.save(FormDocument.builder()
                        .orgId(orgId)
                        .formType("FORM_FOUR")
                        .fileName(orgId + "_" + FORM_FOUR_FILENAME)
                        .formStatus("SIGNED")
                        .filePath(filePath.toString().replace("\\", "/"))
                        .createdBy(request.getApproverUserId())
                        .build());

                response.setFilePath(filePath.toString().replace("\\", "/"));
                log.info("PDF saved at {}", filePath.toString());
            } else {
                log.error("Approval processing failed for OrgId: {}, cannot save PDF", orgId);
                response.setErrorCode(1);
                response.setStatus("FAILED");
                response.setMessage("Approval processing failed, PDF not saved");

            }
            return response;
        } catch (IOException e) {
            log.error("IO error while saving PDF for OrgId: {}", request.getOrgId(), e);
            return buildErrorResponse("-1", "Failed to save PDF file: " + e.getMessage(), "FAILED", request.getOrgId());
        } catch (Exception e) {
            log.error("Error processing approval with file for OrgId: {}", request.getOrgId(), e);
            return buildErrorResponse("-1",
                    "Failed to process approval with file: " + e.getMessage(),
                    "FAILED", request.getOrgId());
        }

    }

    // download signed pdf
    @Override
    public byte[] getSignedPdf(String token, String orgId) {
        try {
            Optional<FormDocument> formDocOpt = formDocumentRepository
                    .findFirstByOrgIdAndFormTypeAndFormStatusOrderByFormDocIdDesc(orgId, "FORM_FOUR", "SIGNED");
            if (formDocOpt.isPresent()) {
                String filePath = formDocOpt.get().getFilePath();
                return Files.readAllBytes(Paths.get(filePath));
            } else {
                log.error("No signed Form 4 found for OrgId: {}", orgId);
                throw new RuntimeException("No signed Form 4 found for OrgId: " + orgId);
            }
        } catch (IOException e) {
            log.error("Error reading signed PDF for OrgId: {}", orgId, e);
            throw new RuntimeException("Failed to read signed PDF for OrgId: " + orgId, e);
        }
    }

    // download unsigned pdf
    @Override
    public byte[] getUnsignedPdf(String token, String orgId) {
        try {
            Optional<FormDocument> formDocOpt = formDocumentRepository
                    .findFirstByOrgIdAndFormTypeAndFormStatusOrderByFormDocIdDesc(orgId, "FORM_FOUR", "UNSIGNED");
            if (formDocOpt.isPresent()) {
                String filePath = formDocOpt.get().getFilePath();
                System.out.println("Unsigned PDF file path: " + filePath);
                return Files.readAllBytes(Paths.get(filePath));
            } else {
                log.error("No unsigned Form 4 found for OrgId: {}", orgId);
                throw new RuntimeException("No unsigned Form 4 found for OrgId: " + orgId);
            }
        } catch (IOException e) {
            log.error("Error reading unsigned PDF for OrgId: {}", orgId, e);
            throw new RuntimeException("Failed to read unsigned PDF for OrgId: " + orgId, e);
        }
    }

    /**
     * Handle approval logic including status update, market mapping, and form
     * generation
     */
    private void handleApproval(RegistrationMstr registration, ApproveRejectRegistrationDTO request,
            String newStatus) {

        /*
         * if ("FINAL_APPROVER".equalsIgnoreCase(request.getApproverType()) &&
         * !registration.isLicenseExists() && request.getRegFeeValidity() != null) {
         * validationUtil.validateFeeValidity(request.getRegFeeValidity(),
         * request.getOrgId());
         * registration.setRegFeeValidity(java.sql.Timestamp.valueOf(
         * request.getRegFeeValidity().atStartOfDay()));
         * }
         */

        // Step 1: Create registration status record
        createRegistrationStatus(registration, newStatus, request);

        /*
         * // Step 2: Create user-market mapping for approved organizations
         * createUserMarketMapping(registration);
         * 
         * // Step 3: Generate forms four
         * 
         * return generateFormFour(registration);
         */

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
    /*
     * private void createUserMarketMapping(RegistrationMstr registration) {
     * try {
     * // Check if mapping already exists
     * String userId = "U_" + registration.getOrgId();
     * 
     * UserMapping existingMapping = userMappingRepository.findByUserId(userId);
     * if (existingMapping != null) {
     * log.info("User mapping already exists for userId: {}", userId);
     * return;
     * }
     * 
     * UserMapping userMapping = new UserMapping();
     * userMapping.setUserId(userId);
     * userMapping.setMarketCode(registration.getOrgBaseMarket());
     * userMapping.setOrgId(registration.getOrgId());
     * userMapping.setIsActive("Y");
     * userMapping.setCreatedOn(new java.sql.Timestamp(System.currentTimeMillis()));
     * 
     * userMappingRepository.save(userMapping);
     * log.info("User-market mapping created for userId: {}, marketCode: {}",
     * userId, registration.getOrgBaseMarket());
     * 
     * } catch (Exception e) {
     * log.error("Error creating user-market mapping for OrgId: {}",
     * registration.getOrgId(), e);
     * throw new ApprovalWorkflowException("USER_MAPPING_ERROR",
     * "Failed to create user-market mapping: " + e.getMessage());
     * }
     * }
     */
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
                        .formStatus("UNSIGNED")
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
                    .createdOn(java.time.LocalDateTime.now())
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
            return REGISTRATION_STATUS.FINAL_APPROVED;
        }
        return REGISTRATION_STATUS.PENDING; // Default fallback
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
