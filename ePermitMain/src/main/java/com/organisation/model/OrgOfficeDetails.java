package com.organisation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_mst_office_details")
public class OrgOfficeDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String districtCode;    // 01 - Alipurduar ( table - un_state_district_mstr)
	private String orgCategory;  	// TRD, BKR, MSR, WHM etc
	private String orgOfficeCode;   // RMCM -> M for Malda
	private String orgOfficeName; 	// RMC MALDA
	
}


