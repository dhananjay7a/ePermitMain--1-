package com.epermit.register.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DocumentUploadDTO {
	private String orgId;
	private String docType;
	private MultipartFile file;
}

