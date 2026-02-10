package com.organisation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tbl_additional_details")
public class RegisterAdditionalDetails {

	@Id
	private String orgId;

	@Column(name="is_bg_ready")
	private String isBGReady;
	
	private String commodityCode;
	private String landDetails;
	private String isRegistered;
	private String registeredWith;
	private String registrationDetails;
	private String marketYearApp;
	private String orgValidFrom;
	private String regFeeValidity;
	private String orgContactPerson;
	private String impExp;
	private String impExpCommodities;
	private String amenitiesDetails;
	private String transactionDetails;
	private String hasPreviousExp;
	private String prevExpRemarks;
	private String hadLicense;
	private String prevLicenseRemarks;
	private String hadMarketLicense;
	private String prevMarketLicenseRemarks;
	private String isGuilty;
	private String guiltyRemarks;
	private String godownDetailsRemarks;
	private String privateMarketYardArea;
	private String styleForPrivateMarketYard;
	private String situationPrivateMarketYard;
	private String natureOfInterestOnLand;
	private String employeeDetails;
	private String official1;
	private String official1Father;
	private String official2;
	private String official2Father;
	private String official3;
	private String official3Father;

	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime modifiedOn;

	private String createdBy;
	private String modifiedBy;

}

