package com.register.model;

import java.util.Date;

public class RegisterBasicInfo {

	// Basic Info
	private String orgRequestStatus;
	private boolean unifiedLicense;
	private String orgId;
	private String userId;
	private String orgCategory;
	private String orgName;
	private String orgType;
	private Date orgConsttnDate;
	private String orgBaseMarket;
	private String orgDocType;
	private String orgDocNo;
	private String isOwner; // "T" or "F"
	private Date regFeeValidity;
	private Date createdOn;
	private Date updatedOn;
	private String createdBy;
	private String updatedBy;
	private String sourceSystem; // e.g., WEB, MOBILE, API

	// Getters and Setters
	public String getOrgRequestStatus() {
		return orgRequestStatus;
	}

	public void setOrgRequestStatus(String orgRequestStatus) {
		this.orgRequestStatus = orgRequestStatus;
	}

	public boolean isUnifiedLicense() {
		return unifiedLicense;
	}

	public void setUnifiedLicense(boolean unifiedLicense) {
		this.unifiedLicense = unifiedLicense;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public Date getOrgConsttnDate() {
		return orgConsttnDate;
	}

	public void setOrgConsttnDate(Date orgConsttnDate) {
		this.orgConsttnDate = orgConsttnDate;
	}

	public String getOrgBaseMarket() {
		return orgBaseMarket;
	}

	public void setOrgBaseMarket(String orgBaseMarket) {
		this.orgBaseMarket = orgBaseMarket;
	}

	public String getOrgDocType() {
		return orgDocType;
	}

	public void setOrgDocType(String orgDocType) {
		this.orgDocType = orgDocType;
	}

	public String getOrgDocNo() {
		return orgDocNo;
	}

	public void setOrgDocNo(String orgDocNo) {
		this.orgDocNo = orgDocNo;
	}

	public String getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(String isOwner) {
		this.isOwner = isOwner;
	}

	public Date getRegFeeValidity() {
		return regFeeValidity;
	}

	public void setRegFeeValidity(Date regFeeValidity) {
		this.regFeeValidity = regFeeValidity;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
}
