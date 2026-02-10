package com.organisation.approvalworkflow.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.organisation.constants.OrgConstants.REGISTRATION_STATUS;

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
            throw new com.organisation.approvalworkflow.exception.ApprovalWorkflowException("NULL_VALUE",
                    "Current status and approver type must not be null");
        }

        // FINAL_APPROVER must NOT act when DSIGN_PENDING (DP)
        if ("FINAL_APPROVER".equalsIgnoreCase(approverType)
                && REGISTRATION_STATUS.DSIGN_PENDING.equalsIgnoreCase(currentStatus)) {
            throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
                    currentStatus, approverType);
        }

        // Prevent approvers acting on already digitally signed/approved states
        if ("SCRUTINY".equalsIgnoreCase(approverType)
                || "FINAL_APPROVER".equalsIgnoreCase(approverType)
                || "DIGITAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (REGISTRATION_STATUS.DIGITAL_SIGNED.equalsIgnoreCase(currentStatus)
                    || REGISTRATION_STATUS.DIGITAL_APPROVED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        }

        // SCRUTINY can act only from PENDING, SUBMITTED, or ENROLLED
        if ("SCRUTINY".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.PENDING.equalsIgnoreCase(currentStatus)
                    && !REGISTRATION_STATUS.SUBMITTED.equalsIgnoreCase(currentStatus)
                    && !REGISTRATION_STATUS.ENROLLED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        } else if ("FINAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.SCRUTINY.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        } else if ("DIGITAL_APPROVER".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.FINAL_APPROVED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
                        currentStatus, approverType);
            }
        } else if ("DIGITAL_SIGN".equalsIgnoreCase(approverType)) {
            if (!REGISTRATION_STATUS.DIGITAL_APPROVED.equalsIgnoreCase(currentStatus)) {
                throw new com.organisation.approvalworkflow.exception.InvalidStatusTransitionException(
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

    /**
     * Check if organization category is a member category
     */
    /*
     * public boolean isMemberCategory(String orgCategory) {
     * if (orgCategory == null || orgCategory.isEmpty()) {
     * return false;
     * }
     * // Non-member categories
     * String[] nonMemberCategories = {
     * "NON_MEMBER",
     * "RENTER",
     * "TEMPORARY"
     * };
     * 
     * for (String category : nonMemberCategories) {
     * if (category.equalsIgnoreCase(orgCategory)) {
     * return false;
     * }
     * }
     * return true;
     * }
     */

    /**
     * Check if organization requires Form 5 (Private Market)
     */
    /*
     * public boolean requiresFormFive(String orgCategory) {
     * return "PRIVATE_MARKET".equalsIgnoreCase(orgCategory) ||
     * "PRIVATE_MARKET_APPLICATION".equalsIgnoreCase(orgCategory);
     * }
     * 
     * /**
     * Check if organization requires Form 4 (Regular markets)
     */
    /*
     * public boolean requiresFormFour(String orgCategory) {
     * return !requiresFormFive(orgCategory) && isMemberCategory(orgCategory);
     * }
     */

}

class ApprovalWorkflowException extends RuntimeException {
    ApprovalWorkflowException(String code, String message) {
        super(message);
    }
}
