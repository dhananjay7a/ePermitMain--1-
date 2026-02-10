package com.epermit.register.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BankGstDTO {
	private String orgId;
	private String orgGstNo;
	private String orgGstState;
	private String orgBankName;
	private String orgBankAccNo;
	private String orgIfscCode;
	private String orgBankBranch;
	private LocalDateTime orgCreatedOn;
	private String orgCreatedBy;
	private LocalDateTime orgModifiedOn;
	private String orgModifiedBy;
	
}

