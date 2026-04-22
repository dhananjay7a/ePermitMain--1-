package com.organisation.exception;

/**
 * Custom exception for approval workflow operations
 */
public class ApprovalWorkflowException extends RuntimeException {

    private final String errorCode;

    public ApprovalWorkflowException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApprovalWorkflowException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
