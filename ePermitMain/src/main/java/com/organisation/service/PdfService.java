package com.organisation.service;

import org.springframework.http.ResponseEntity;

import com.organisation.dto.SignedPdfRequestDto;

public interface PdfService {
	 void saveSignedPdf(SignedPdfRequestDto dto, String token) throws Exception;
	 ResponseEntity<byte[]> downloadSignedPdf(String orgId, String token) throws Exception;

}
