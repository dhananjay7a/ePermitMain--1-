package com.organisation.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Table;



@Table(name = "UN_MARKET_MSTR")
public class MarketMaster extends Pagination {

	@Column(name = "CREATED_ON")
	private java.sql.Timestamp createdOn;

	@Column(name = "MARKET_CODE")
	private String marketCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "STATE_CODE")
	private String stateCode;

	@Column(name = "DISTRICT_CODE")
	private String districtCode;

	private String districtName;

//-------------------BANK DETAILS------------------------------	
	@Column(name = "BANK_NAME")
	private String bankName;

	@Column(name = "BRANCH_CODE")
	private String branchCode;

	@Column(name = "IFSC_CODE")
	private String ifscCode;

	@Column(name = "BANK_ACC_NO")
	private String bankAccNo;

	@Column(name = "ACCOUNT_NAME")
	private String accountName;

	@Column(name = "BRANCH_NAME")
	private String branchName;
	// --------------------------------------------------------------
	@Column(name = "CREATED_BY")
	private String createdBy;

//-------------------------AUTHORIZED PERSON DETAILS------------------------------------
	@Column(name = "AUTHORIZED_PERSON")
	private String authorizedPerson;

	@Column(name = "TELE_PHONE_NO")
	private String telePhoneNo;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "CITY")
	private String city;

	@Column(name = "PINCODE")
	private String pincode;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	private String shortName;
	private String marketPassword;
	private String registrationNo;
	private java.sql.Timestamp modifiedOn;
	private BigDecimal roundOff;
	private String smsMobileNo;
	private String defaultCommodity;
	private String defaultPurpose;
	private String purchaseBillFormat;
	private String settVoucherFormat;
	private String marketLanguage;
	private String isDocOnline;
	private String issendSmsReqd;
	private String isMockMarket;
	private boolean restrictDenotifiedMkt;
	private String isDenotifiedMarket;
	private String modified_by;
	private String groupCode;
	private String dropdownValue;
	//kanchang added
	private String checkPostId;
	private String checkPostName;

	private String returnMsg;
	private String destinationType;
	@Column(name = "is_procurement_center")
	private String isProcurementCenter;
	@Column(name = "agency_code")
	private String agencyCode;
	
	private String permitCnclStatus; //PraveenL
	
	public String getIsDenotifiedMarket() {
		return isDenotifiedMarket;
	}

	public void setIsDenotifiedMarket(String isDenotifiedMarket) {
		this.isDenotifiedMarket = isDenotifiedMarket;
	}

	public boolean isRestrictDenotifiedMkt() {
		return restrictDenotifiedMkt;
	}

	public void setRestrictDenotifiedMkt(boolean restrictDenotifiedMkt) {
		this.restrictDenotifiedMkt = restrictDenotifiedMkt;
	}

	public BigDecimal getRoundOff() {
		return roundOff;
	}

	public void setRoundOff(BigDecimal roundOff) {
		this.roundOff = roundOff;
	}

	public java.sql.Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(java.sql.Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public java.sql.Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(java.sql.Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getMarketCode() {
		return marketCode;
	}

	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getMarketPassword() {
		return marketPassword;
	}

	public void setMarketPassword(String marketPassword) {
		this.marketPassword = marketPassword;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getTelePhoneNo() {
		return telePhoneNo;
	}

	public void setTelePhoneNo(String telePhoneNo) {
		this.telePhoneNo = telePhoneNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAuthorizedPerson() {
		return authorizedPerson;
	}

	public void setAuthorizedPerson(String authorizedPerson) {
		this.authorizedPerson = authorizedPerson;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getSmsMobileNo() {
		return smsMobileNo;
	}

	public void setSmsMobileNo(String smsMobileNo) {
		this.smsMobileNo = smsMobileNo;
	}

	public String getDefaultCommodity() {
		return defaultCommodity;
	}

	public void setDefaultCommodity(String defaultCommodity) {
		this.defaultCommodity = defaultCommodity;
	}

	public String getDefaultPurpose() {
		return defaultPurpose;
	}

	public void setDefaultPurpose(String defaultPurpose) {
		this.defaultPurpose = defaultPurpose;
	}

	public String getPurchaseBillFormat() {
		return purchaseBillFormat;
	}

	public void setPurchaseBillFormat(String purchaseBillFormat) {
		this.purchaseBillFormat = purchaseBillFormat;
	}

	public String getSettVoucherFormat() {
		return settVoucherFormat;
	}

	public void setSettVoucherFormat(String settVoucherFormat) {
		this.settVoucherFormat = settVoucherFormat;
	}

	public String getMarketLanguage() {
		return marketLanguage;
	}

	public void setMarketLanguage(String marketLanguage) {
		this.marketLanguage = marketLanguage;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getIsDocOnline() {
		return isDocOnline;
	}

	public void setIsDocOnline(String isDocOnline) {
		this.isDocOnline = isDocOnline;
	}

	public String getIssendSmsReqd() {
		return issendSmsReqd;
	}

	public void setIssendSmsReqd(String issendSmsReqd) {
		this.issendSmsReqd = issendSmsReqd;
	}

	public String getIsMockMarket() {
		return isMockMarket;
	}

	public void setIsMockMarket(String isMockMarket) {
		this.isMockMarket = isMockMarket;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModified_by() {
		return modified_by;
	}

	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	 public String getDistrictName() { return districtName; } 
	 public void setDistrictName(String districtName) { this.districtName = districtName; }

	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getIsProcurementCenter() {
		return isProcurementCenter;
	}

	public void setIsProcurementCenter(String isProcurementCenter) {
		this.isProcurementCenter = isProcurementCenter;
	}

	public String getAgencyCode() {
		return agencyCode;
	}

	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}

	public String getCheckPostId() {
		return checkPostId;
	}

	public void setCheckPostId(String checkPostId) {
		this.checkPostId = checkPostId;
	}

	public String getCheckPostName() {
		return checkPostName;
	}

	public void setCheckPostName(String checkPostName) {
		this.checkPostName = checkPostName;
	}

	public String getPermitCnclStatus() {
		return permitCnclStatus;
	}
	
	public void setPermitCnclStatus(String permitCnclStatus) {
		this.permitCnclStatus = permitCnclStatus;
	}

	public String getDropdownValue() {
		return dropdownValue;
	}

	public void setDropdownValue(String dropdownValue) {
		this.dropdownValue = dropdownValue;
	}

	

}
