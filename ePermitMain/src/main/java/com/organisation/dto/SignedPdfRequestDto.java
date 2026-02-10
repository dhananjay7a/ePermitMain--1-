package com.organisation.dto;

import lombok.Data;

@Data
public class SignedPdfRequestDto {
	
	    private String orgId;

	    
	    private String signedPdfBase64;
}
