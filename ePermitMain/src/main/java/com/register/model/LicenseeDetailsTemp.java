package com.register.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_license_details_temp")
public class LicenseeDetailsTemp {
	
	@Id
	private String orgId;
	private String licenseNo;
	private String licenseBookNo;
	private String licenseReceiptBookNo;
	private String licenseReceiptNo;
	private String orgValidFrom;
	private String regFeeValidity;
	
	
}
