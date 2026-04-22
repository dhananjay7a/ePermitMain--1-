package com.organisation.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_bank_gst")
public class BankGstMstr {
	
	@Id
	private String orgId;
	private String orgBackAccNo;
	private String orgIFSCCode;
	
	private String orgGSTNo;
	private String orgGSTState;
	
	private String docType;
	private String docNo;
	
	private String orgVirtualAccNo;
	
	private LocalDateTime createdOn;
	private String createdBy;
	
}


