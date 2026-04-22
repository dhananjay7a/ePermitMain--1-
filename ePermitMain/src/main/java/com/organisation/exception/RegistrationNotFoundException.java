package com.organisation.exception;

/**
 * Exception when registration is not found
 */
public class RegistrationNotFoundException extends ApprovalWorkflowException {

    public RegistrationNotFoundException(String orgId) {
        super("REGISTRATION_NOT_FOUND",
                String.format("Registration with Organization ID '%s' not found", orgId));
    }

}
