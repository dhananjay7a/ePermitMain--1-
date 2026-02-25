package com.organisation.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.service.PDFGenerationService;

@Service("form4Service")
public class Form4 {

    @Autowired
    private PDFGenerationService pdfGenerationService;

    public String createPdf(RegistrationMstr regMstr) {
        return pdfGenerationService.generateFormFour(regMstr);
    }
}
