package com.organisation.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;


@Entity
@Table(name="tbl_mst_registration")
public class RegistrationMstr {
	
	//	----------- Basic Info-------------
	@Id
	private String orgId;
	private String orgName;
	private String orgOfficeCode;  // eg - RMCDJ (RMC DARJEELING) , TRDMR ( Trader Murshidabad)
	
	@Column(name = "is_unified")
	private boolean unifiedLicense;
	
    private boolean hasExistingLicense;  // work on it later
	private String orgType;
	private Date orgConsttnDate;
	private String orgBaseMarket;

	//	----------- Permanent Address -------------
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
	
	//	----------- Extra fields-------------
	private String orgIsActive;
	private String isMock;
	
	
	private String orgCategory;
	
	private String orgCategoryScope;
	private Timestamp orgValidFrom;
	private Timestamp regFeeValidity;
	private boolean isLicenseExists;
	private String orgRenewalStatus;
	private Timestamp requestStartDate;
	private Timestamp requestEndDate;
	@Column(columnDefinition = "TEXT")
	private String financialYear;
	private String regBookType;
	private String regReceiptNo;
	private Boolean isRegistraionFeePaid;
	private Boolean isRenewalFeePaid;
	private String isRenewed;
	private Timestamp renewalDate;
	private Integer renewedCount;
	private Timestamp registrationSubmitDate; 
	private BigDecimal regFeeAmount;
	 


	private LocalDateTime createdOn;
	private String createdBy;
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgOfficeCode() {
		return orgOfficeCode;
	}
	public void setOrgOfficeCode(String orgOfficeCode) {
		this.orgOfficeCode = orgOfficeCode;
	}
	public boolean isUnifiedLicense() {
		return unifiedLicense;
	}
	public void setUnifiedLicense(boolean unifiedLicense) {
		this.unifiedLicense = unifiedLicense;
	}
	public boolean isHasExistingLicense() {
		return hasExistingLicense;
	}
	public void setHasExistingLicense(boolean hasExistingLicense) {
		this.hasExistingLicense = hasExistingLicense;
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
	public String getOrgApplicantName() {
		return orgApplicantName;
	}
	public void setOrgApplicantName(String orgApplicantName) {
		this.orgApplicantName = orgApplicantName;
	}
	public String getOrgApplicantParentName() {
		return orgApplicantParentName;
	}
	public void setOrgApplicantParentName(String orgApplicantParentName) {
		this.orgApplicantParentName = orgApplicantParentName;
	}
	public String getOrgAddress() {
		return orgAddress;
	}
	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}
	public String getOrgState() {
		return orgState;
	}
	public void setOrgState(String orgState) {
		this.orgState = orgState;
	}
	public String getOrgCity() {
		return orgCity;
	}
	public void setOrgCity(String orgCity) {
		this.orgCity = orgCity;
	}
	public String getOrgDist() {
		return orgDist;
	}
	public void setOrgDist(String orgDist) {
		this.orgDist = orgDist;
	}
	public String getOrgPoliceStation() {
		return orgPoliceStation;
	}
	public void setOrgPoliceStation(String orgPoliceStation) {
		this.orgPoliceStation = orgPoliceStation;
	}
	public String getOrgPostOffice() {
		return orgPostOffice;
	}
	public void setOrgPostOffice(String orgPostOffice) {
		this.orgPostOffice = orgPostOffice;
	}
	public String getOrgMobileNo() {
		return orgMobileNo;
	}
	public void setOrgMobileNo(String orgMobileNo) {
		this.orgMobileNo = orgMobileNo;
	}
	public String getOrgPin() {
		return orgPin;
	}
	public void setOrgPin(String orgPin) {
		this.orgPin = orgPin;
	}
	public String getOrgBlockName() {
		return orgBlockName;
	}
	public void setOrgBlockName(String orgBlockName) {
		this.orgBlockName = orgBlockName;
	}
	public String getOrgIsActive() {
		return orgIsActive;
	}
	public void setOrgIsActive(String orgIsActive) {
		this.orgIsActive = orgIsActive;
	}
	public String getIsMock() {
		return isMock;
	}
	public void setIsMock(String isMock) {
		this.isMock = isMock;
	}
	public String getOrgCategory() {
		return orgCategory;
	}
	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}
	public String getOrgCategoryScope() {
		return orgCategoryScope;
	}
	public void setOrgCategoryScope(String orgCategoryScope) {
		this.orgCategoryScope = orgCategoryScope;
	}
	public Timestamp getOrgValidFrom() {
		return orgValidFrom;
	}
	public void setOrgValidFrom(Timestamp orgValidFrom) {
		this.orgValidFrom = orgValidFrom;
	}
	public Timestamp getRegFeeValidity() {
		return regFeeValidity;
	}
	public void setRegFeeValidity(Timestamp regFeeValidity) {
		this.regFeeValidity = regFeeValidity;
	}
	public boolean isLicenseExists() {
		return isLicenseExists;
	}
	public void setLicenseExists(boolean isLicenseExists) {
		this.isLicenseExists = isLicenseExists;
	}
	public String getOrgRenewalStatus() {
		return orgRenewalStatus;
	}
	public void setOrgRenewalStatus(String orgRenewalStatus) {
		this.orgRenewalStatus = orgRenewalStatus;
	}
	public Timestamp getRequestStartDate() {
		return requestStartDate;
	}
	public void setRequestStartDate(Timestamp requestStartDate) {
		this.requestStartDate = requestStartDate;
	}
	public Timestamp getRequestEndDate() {
		return requestEndDate;
	}
	public void setRequestEndDate(Timestamp requestEndDate) {
		this.requestEndDate = requestEndDate;
	}
	
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	public Boolean isRenewalFeePaid() {
		return isRenewalFeePaid;
	}
	public void setRenewalFeePaid(Boolean isRenewalFeePaid) {
		this.isRenewalFeePaid = isRenewalFeePaid;
	}
	public String getIsRenewed() {
		return isRenewed;
	}
	public void setIsRenewed(String isRenewed) {
		this.isRenewed = isRenewed;
	}
	public Timestamp getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Timestamp renewalDate) {
		this.renewalDate = renewalDate;
	}
	public Integer getRenewedCount() {
		return renewedCount;
	}
	public void setRenewedCount(Integer renewedCount) {
		this.renewedCount = renewedCount;
	}
	public Timestamp getRegistrationSubmitDate() {
		return registrationSubmitDate;
	}
	public void setRegistrationSubmitDate(Timestamp registrationSubmitDate) {
		this.registrationSubmitDate = registrationSubmitDate;
	}
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getRegBookType() {
		return regBookType;
	}
	public void setRegBookType(String regBookType) {
		this.regBookType = regBookType;
	}
	public String getRegReceiptNo() {
		return regReceiptNo;
	}
	public void setRegReceiptNo(String regReceiptNo) {
		this.regReceiptNo = regReceiptNo;
	}
	public BigDecimal getRegFeeAmount() {
		return regFeeAmount;
	}
	public void setRegFeeAmount(BigDecimal regFeeAmount) {
		this.regFeeAmount = regFeeAmount;
	}
	public Boolean getIsRegistraionFeePaid() {
		return isRegistraionFeePaid;
	}
	public void setIsRegistraionFeePaid(Boolean isRegistraionFeePaid) {
		this.isRegistraionFeePaid = isRegistraionFeePaid;
	}

	

	
	
	
	
}


