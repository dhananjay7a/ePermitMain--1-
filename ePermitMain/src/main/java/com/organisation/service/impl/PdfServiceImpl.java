package com.organisation.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.organisation.controller.PdfController;
import com.organisation.dto.SignedPdfRequestDto;
import com.organisation.model.RegistrationPdf;
import com.organisation.repository.RegistrationPdfRepository;
import com.organisation.security.TokenService;
import com.organisation.service.PdfService;

import io.jsonwebtoken.Claims;
@Service
public class PdfServiceImpl implements PdfService {
	
	@Autowired
	private TokenService ts;
	@Value("${file.signed.pdf.dir}")
	private String signedPdfDir;
	
	@Autowired
	private RegistrationPdfRepository registrationPdfRepo;

	private static final Logger log = LoggerFactory.getLogger(PdfServiceImpl.class);
	 @Override
	    public void saveSignedPdf(SignedPdfRequestDto dto, String token) throws Exception {

	        // 🔐 Token handling
	        if (token != null && token.startsWith("Bearer ")) {
	            token = token.substring(7);
	        }

	        Claims claims = ts.validateToken(token);
	        if (claims == null) {
	            throw new RuntimeException("Invalid token");
	        }

	        String userId = ts.extractUserId(token);
	        String orgIdFromToken = userId.startsWith("U") ? userId.substring(1) : userId;

	        // 🔎 Security check
	        if (!orgIdFromToken.equals(dto.getOrgId())) {
	            throw new RuntimeException("OrgId mismatch");
	        }

	        // 📄 Decode Base64 PDF
	        byte[] pdfBytes = Base64.getDecoder()
	                                .decode(dto.getSignedPdfBase64());

	        // 📁 Ensure directory exists
	        Path dirPath = Paths.get(signedPdfDir);

	        if (!Files.exists(dirPath)) {
	            Files.createDirectories(dirPath);
	        }

	        // 📌 Save file
	        String fileName = dto.getOrgId() + "_SIGNED.pdf";
	        Path filePath = dirPath.resolve(fileName);
	        Files.write(filePath, pdfBytes);

	        // 🗄️ Save DB entry
	        RegistrationPdf pdf = new RegistrationPdf();
	        pdf.setOrgId(orgIdFromToken);
	        pdf.setPdfPath(filePath.toString());
	        pdf.setStatus("SIGNED");
	        pdf.setCreatedOn(LocalDateTime.now());

	        registrationPdfRepo.save(pdf);

	        log.info("Signed PDF saved successfully for orgId={}", dto.getOrgId());
	    }
	 
	 
	 @Override
	    public ResponseEntity<byte[]> downloadSignedPdf(String orgId, String token)
	            throws Exception {

	        // 🔐 Token validation
	        if (token != null && token.startsWith("Bearer ")) {
	            token = token.substring(7);
	        }

	        Claims claims = ts.validateToken(token);
	        if (claims == null) {
	            throw new RuntimeException("Invalid token");
	        }

	        String userId = ts.extractUserId(token);
	        String tokenOrgId = userId.startsWith("U") ? userId.substring(1) : userId;

	        // 🔒 Security check
	        if (!tokenOrgId.equals(orgId)) {
	            throw new RuntimeException("Unauthorized access");
	        }

	        // 📄 Get PDF path from DB
	        RegistrationPdf pdf = registrationPdfRepo.findByOrgId(orgId)
	                .orElseThrow(() -> new RuntimeException("Signed PDF not found"));

	        Path filePath = Paths.get(pdf.getPdfPath());

	        if (!Files.exists(filePath)) {
	            throw new RuntimeException("PDF file missing on server");
	        }

	        byte[] fileBytes = Files.readAllBytes(filePath);

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=" + orgId + "_SIGNED.pdf")
	                .body(fileBytes);
	    }
}
