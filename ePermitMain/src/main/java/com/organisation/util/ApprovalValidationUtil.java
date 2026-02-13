package com.organisation.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.organisation.constants.OrgConstants.REGISTRATION_STATUS;
import com.organisation.exception.ApprovalWorkflowException;

import java.time.LocalDate;

/**
 * Utility class for validation operations in approval workflow
 */
@Component
@Slf4j
public class ApprovalValidationUtil {

    /**
     * Validate registration status transition
     */
    public void validateStatusTransition(String currentStatus, String approverType) {

        if (currentStatus == null || approverType == null) {
            throw new com.organisation.exception.ApprovalWorkflowException("NULL_VALUE",
                    "Current status and approver type must not be null");
        }

        // FINAL_APPROVER must NOT act when DSIGN_PENDING (DP)
        if ("FINAL_APPROVER".equalsIgnoreCase(approverType)
                && REGISTRATION_STATUS.DSIGN_PENDING.equalsIgnoreCase(currentStatus)) {
            throw new com.organisation.exception.InvalidStatusTransitionException(
                    currentStatus, approverType);
        }

        // Prevent approvers acting on already digitally signed/approved states
        if ("SCRUTINY".equalsIgnoreCase(approverType)
                || "FINAL_APPROVER".equalsIgnoreCase(approverType)
                || "DIGITAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (REGISTRATION_STATUS.DIGITAL_SIGNED.equalsIgnoreCase(currentStatus)
                    || REGISTRATION_STATUS.DIGITAL_APPROVED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        }

        // SCRUTINY can act only from PENDING, SUBMITTED, or ENROLLED
        if ("SCRUTINY".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.PENDING.equalsIgnoreCase(currentStatus)
                    && !REGISTRATION_STATUS.SUBMITTED.equalsIgnoreCase(currentStatus)
                    && !REGISTRATION_STATUS.SAVED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        } else if ("FINAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.SCRUTINY.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        } else if ("DIGITAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.DSIGN_PENDING.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        }
    }

    /**
     * Validate fee validity period
     */
    public void validateFeeValidity(LocalDate sentValidity, String orgId) {
        if (sentValidity == null) {
            throw new ApprovalWorkflowException("VALIDITY_REQUIRED", "Validity date is required");
        }

        LocalDate now = LocalDate.now();
        // Validity must be within 1 year back and 3 years forward
        if (sentValidity.isBefore(now.minusYears(1)) || sentValidity.isAfter(now.plusYears(3))) {
            throw new ApprovalWorkflowException("VALIDITY_EXCEEDS_MAX_LIMIT",
                    "Validity date is out of allowed range. Must be between " +
                            now.minusYears(1) + " and " + now.plusYears(3));
        }
    }

}
