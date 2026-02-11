// package com.organisation.controller;

// import com.organisation.dto.ApproveRejectRegistrationDTO;
// import com.organisation.dto.ApprovalResponseDTO;
// import com.organisation.service.ApprovalWorkflowService;
// import com.epermit.register.responsehandler.ApiResponses;
// import com.epermit.register.responsehandler.ResponseBean;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// /**
// * REST Controller for registration approval/rejection operations
// * Handles both encrypted and plain JSON requests
// */
// @RestController
// @RequestMapping("/api/approvals")
// @Validated
// @Slf4j
// public class ApprovalWorkflowController {

// @Autowired
// private ApprovalWorkflowService approvalWorkflowService;

// @PostMapping("/approveRejectRegistration/v2")
// public ResponseEntity<ApiResponses>
// approveRejectRegistration(@RequestHeader("Authorization") String token,
// @RequestBody ApproveRejectRegistrationDTO request) {

// ResponseBean responseBean = new ResponseBean();

// try {
// log.info("Received approval request for OrgId: {}", request.getOrgId());

// // Process approval

// ApprovalResponseDTO response = approvalWorkflowService
// .approveRejectRegistration(token, request);
// if (response == null || response.getErrorCode() < 0) {
// log.error("Approval processing failed for OrgId: {}", request.getOrgId());
// responseBean.AllResponse("Error", response.getMessage());
// return new ResponseEntity<>(responseBean.getResponse(),
// HttpStatus.INTERNAL_SERVER_ERROR);
// }
// responseBean.AllResponse("Success", response);

// log.info("Approval request processed successfully for OrgId: {}",
// request.getOrgId());

// return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.OK);

// } catch (Exception e) {
// log.error("Error processing plain text approval request", e);
// responseBean.AllResponse("Error", e.getMessage());
// return new ResponseEntity<>(responseBean.getResponse(),
// HttpStatus.INTERNAL_SERVER_ERROR);
// }
// }

// /**
// * Get approval history for a specific organization
// * Endpoint: GET /api/approvals/history/{orgId}
// */
// @GetMapping("/history/{orgId}")
// public ResponseEntity<?> getApprovalHistory(@PathVariable String orgId) {

// try {
// log.info("Retrieving approval history for OrgId: {}", orgId);

// Object history = approvalWorkflowService.getApprovalHistory(orgId);

// log.info("Approval history retrieved successfully for OrgId: {}", orgId);

// return ResponseEntity.ok(history);

// } catch (Exception e) {
// log.error("Error retrieving approval history for OrgId: {}", orgId, e);
// return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
// "RETRIEVAL_ERROR", "Failed to retrieve approval history: " + e.getMessage());
// }
// }

// /**
// * Health check endpoint for approval service
// * Endpoint: GET /api/approvals/health
// */
// @GetMapping("/health")
// public ResponseEntity<?> health() {
// Map<String, Object> response = new HashMap<>();
// response.put("status", "UP");
// response.put("service", "ApprovalWorkflow");
// response.put("timestamp", System.currentTimeMillis());
// return ResponseEntity.ok(response);
// }

// /**
// * Build error response in standard format
// */
// private ResponseEntity<?> buildErrorResponse(HttpStatus status, String
// errorCode, String message) {
// ApprovalResponseDTO errorResponse = ApprovalResponseDTO.builder()
// .status("FAILED")
// .errorCode(errorCode.hashCode())
// .message(message)
// .build();

// return ResponseEntity.status(status).body(errorResponse);
// }

// }
