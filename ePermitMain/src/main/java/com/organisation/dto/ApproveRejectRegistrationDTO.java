package com.organisation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Approve/Reject Registration Request
 * Adapted to ePermit project naming conventions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRejectRegistrationDTO {

    private String orgId;

    private String regStatus; // 'a' for Approve or 'r' for Reject

    private String approverType; // SCRUTINY, FINAL_APPROVER, DIGITAL_APPROVER

    private LocalDate regFeeValidity; // Optional for some flows

    private String remarks; // Optional remarks for approval/rejection

    private String approverUserId; // Acting approver user ID

}
