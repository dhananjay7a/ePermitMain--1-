package com.organisation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organisation.dto.SignedPdfRequestDto;
import com.organisation.service.PdfService;


@RestController
@RequestMapping("/pdf")
public class PdfController {
	
	@Autowired
	private PdfService pdfService;
	
	private static final Logger log = LoggerFactory.getLogger(PdfController.class);
	
	  @PostMapping("/upload-signed")
	    public ResponseEntity<?> uploadSignedPdf(
	            @RequestBody SignedPdfRequestDto dto,
	            @RequestHeader("Authorization") String token) {
		  
		 
	        log.info("Upload signed PDF request for orgId={}", dto.getOrgId());

	        try {
	            pdfService.saveSignedPdf(dto, token);
	            return ResponseEntity.ok("Signed PDF uploaded successfully");

	        } catch (Exception e) {
	            log.error("Error uploading signed PDF", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to upload signed PDF");
	        }
	    }
	  @GetMapping("/download-signed/{orgId}")
	  public ResponseEntity<byte[]> downloadSignedPdf(
	            @PathVariable String orgId,
	            @RequestHeader("Authorization") String token) {

	        try {
	            return pdfService.downloadSignedPdf(orgId, token);

	        } catch (Exception e) {
	            log.error("Error downloading signed PDF for orgId={}", orgId, e);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	    }
	  

}
