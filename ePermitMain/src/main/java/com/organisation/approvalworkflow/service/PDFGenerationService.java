package com.organisation.approvalworkflow.service;

import com.organisation.model.RegistrationMstr;

public interface PDFGenerationService {
    String generateFormFour(RegistrationMstr registration);

}