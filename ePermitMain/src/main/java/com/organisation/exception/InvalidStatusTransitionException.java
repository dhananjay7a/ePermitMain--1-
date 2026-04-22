package com.organisation.exception;

/**
 * Exception for invalid status transitions during approval workflow
 */
public class InvalidStatusTransitionException extends ApprovalWorkflowException {

    public InvalidStatusTransitionException(String currentStatus, String approverType) {
        super("INVALID_STATUS_TRANSITION",
                String.format("Invalid status transition for approver type %s from current status %s",
                        approverType, currentStatus));
    }

}
