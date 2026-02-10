package com.organisation.approvalworkflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Approval/Rejection operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResponseDTO {

    private long errorCode;
    private String message;
    private String status; // SUCCESS, FAILED, VALIDATION_FAILED
    private String orgId;
    private String orgName;
    private String newStatus;
    private String fileName; // Generated PDF file name
    private String filePath; // Full path to the generated PDF file

}
