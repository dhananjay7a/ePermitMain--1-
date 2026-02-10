package com.organisation.config;

import com.organisation.dto.ApprovalResponseDTO;
import com.organisation.exception.ApprovalWorkflowException;
import com.organisation.exception.InvalidStatusTransitionException;
import com.organisation.exception.RegistrationNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for Approval Workflow module
 * Handles all exceptions and returns standardized error responses
 */
@ControllerAdvice
@Slf4j
public class ApprovalWorkflowExceptionHandler {

    /**
     * Handle ApprovalWorkflowException
     */
    @ExceptionHandler(ApprovalWorkflowException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApprovalResponseDTO> handleApprovalWorkflowException(
            ApprovalWorkflowException ex) {

        log.error("ApprovalWorkflowException occurred: {} - {}", ex.getErrorCode(), ex.getMessage());

        ApprovalResponseDTO response = ApprovalResponseDTO.builder()
                .status("FAILED")
                .errorCode(ex.getErrorCode().hashCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApprovalResponseDTO> handleInvalidStatusTransitionException(
            InvalidStatusTransitionException ex) {

        log.error("InvalidStatusTransitionException occurred: {} - {}", ex.getErrorCode(), ex.getMessage());

        ApprovalResponseDTO response = ApprovalResponseDTO.builder()
                .status("FAILED")
                .errorCode(ex.getErrorCode().hashCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApprovalResponseDTO> handleRegistrationNotFoundException(
            RegistrationNotFoundException ex) {

        log.error("RegistrationNotFoundException occurred: {} - {}", ex.getErrorCode(), ex.getMessage());

        ApprovalResponseDTO response = ApprovalResponseDTO.builder()
                .status("FAILED")
                .errorCode(ex.getErrorCode().hashCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApprovalResponseDTO> handleGenericException(Exception ex) {

        log.error("Unexpected exception occurred", ex);

        ApprovalResponseDTO response = ApprovalResponseDTO.builder()
                .status("FAILED")
                .errorCode("INTERNAL_ERROR".hashCode())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
