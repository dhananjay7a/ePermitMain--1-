package com.register.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tbl_register_basic_info_address_temp")
public class RegisterBasicInfoAddress {

	// Basic Info Tab
	@Id
	private String userId;
	private String orgId;

	@Column(name = "is_unified")
	private boolean unifiedLicense;

    private boolean hasExistingLicense;  // work on it later
	private String orgCategory;
	private String orgName;
	private String orgType;
	private Date orgConsttnDate;
	private String orgBaseMarket;
	private String orgDocType;
	private String orgDocNo;
	private String orgOfficeCode;  // eg - RMCDJ (RMC DARJEELING) , TRDMR ( Trader Murshidabad)
	@Column(name = "org_created_on", updatable = false)
	private LocalDateTime orgCreatedOn;
	private LocalDateTime orgModifiedOn;
	private String orgCreatedBy;
	private String orgModifiedBy;
	
	// Permanent Address Tab
	private String orgApplicantName;
	private String orgApplicantParentName;
	private String orgAddress;
	private String orgState;
	private String orgCity;
	private String orgDist;
	private String orgPoliceStation;
	private String orgPostOffice;
	private String orgMobileNo;
	private String orgPin;
	private String orgBlockName;

	@Column(name = "address_created_on", updatable = false)
	private LocalDateTime addressCreatedOn;
	private LocalDateTime addressModifiedOn;
	private String addressCreatedBy;
	private String addressModifiedBy;

}
