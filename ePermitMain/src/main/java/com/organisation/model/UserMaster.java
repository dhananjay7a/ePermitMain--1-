package com.organisation.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the USER_MSTR database table.
 * 
 */
public class UserMaster extends Pagination implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private String orgId;
	private String userName;
	private String thumbPrint;

	private String orgBaseMarket;
	private String orgCategory;
	
	private String userCategory;
	private String userPhone;
	private String userMobile;
	private String userEmail;
	private String documentNo;
	private String documentType;
	private String userAddress;
	private String firstLoginFlag;
	private String userPassword;
	private String encPassword;
	private Timestamp passwordExpiryDate;
	private String isActive;
	private String maker;
	private String checker;
	private String userDfltLanguage;
	private String createdBy;
	private Timestamp createdOn;
	private String modifiedBy;
	private Timestamp modifiedOn;
	private String scopeUsr;
	
	// Internal fields
	private String errorMsg;
	private String returnMsg;
	private String orgName;
	private String isLoggedIn;
	private String createOn;
	private String mobChkFlag;
	private String userDfltRole;

	
	//For bulk SMS
	private String msgTxt;
	private String isFarmerMem;
	private String market;
	private String role;
	private String description;
	
	

	private String otpExpiryTimeStr;
	private String lastReqTime;
	private String orgUserScope;
	private String orgStateCode;
	private String orgDistrictCode;
	
	private String createdDateString;
	private String modifiedDateString;
	
	private String collectionType;
	
	private String alias;
	private String issuer;
	
	
	private int noOfAttempt;
	private String isLock;
	private Timestamp lockTimestamp;
	private Timestamp userOtpExpiry;
	private String isOtpReq="N";
	private String isLoginWithOtp;
	private String otp;
	private String userDataVisibility;
	private String userBlockName;
	private String userDistName;
	
	
	public String getUserBlockName() {
		return userBlockName;
	}
	public void setUserBlockName(String userBlockName) {
		this.userBlockName = userBlockName;
	}
	public String getUserDistName() {
		return userDistName;
	}
	public void setUserDistName(String userDistName) {
		this.userDistName = userDistName;
	}
	public String getUserDataVisibility() {
		return userDataVisibility;
	}
	public void setUserDataVisibility(String userDataVisibility) {
		this.userDataVisibility = userDataVisibility;
	}
	public Timestamp getUserOtpExpiry() {
		return userOtpExpiry;
	}
	public void setUserOtpExpiry(Timestamp userOtpExpiry) {
		this.userOtpExpiry = userOtpExpiry;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getCreatedDateString() {
		return createdDateString;
	}
	public void setCreatedDateString(String createdDateString) {
		this.createdDateString = createdDateString;
	}
	public String getModifiedDateString() {
		return modifiedDateString;
	}
	public void setModifiedDateString(String modifiedDateString) {
		this.modifiedDateString = modifiedDateString;
	}
	public String getLastReqTime() {
		return lastReqTime;
	}
	public void setLastReqTime(String lastReqTime) {
		this.lastReqTime = lastReqTime;
	}
	public String getScopeUsr() {
		return scopeUsr;
	}
	public void setScopeUsr(String scopeUsr) {
		this.scopeUsr = scopeUsr;
	}
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCategory() {
		return userCategory;
	}
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public String getFirstLoginFlag() {
		return firstLoginFlag;
	}
	public void setFirstLoginFlag(String firstLoginFlag) {
		this.firstLoginFlag = firstLoginFlag;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getEncPassword() {
		return encPassword;
	}
	public void setEncPassword(String encPassword) {
		this.encPassword = encPassword;
	}
	public Timestamp getPasswordExpiryDate() {
		return passwordExpiryDate;
	}
	public void setPasswordExpiryDate(Timestamp passwordExpiryDate) {
		this.passwordExpiryDate = passwordExpiryDate;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public String getUserDfltLanguage() {
		return userDfltLanguage;
	}
	public void setUserDfltLanguage(String userDfltLanguage) {
		this.userDfltLanguage = userDfltLanguage;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Timestamp getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getIsLoggedIn() {
		return isLoggedIn;
	}
	public void setIsLoggedIn(String isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	public String getCreateOn() {
		return createOn;
	}
	public void setCreateOn(String createOn) {
		this.createOn = createOn;
	}
	public String getMobChkFlag() {
		return mobChkFlag;
	}
	public void setMobChkFlag(String mobChkFlag) {
		this.mobChkFlag = mobChkFlag;
	}
	public String getUserDfltRole() {
		return userDfltRole;
	}
	public void setUserDfltRole(String userDfltRole) {
		this.userDfltRole = userDfltRole;
	}
	public String getOtpExpiryTimeStr() {
		return otpExpiryTimeStr;
	}
	public void setOtpExpiryTimeStr(String otpExpiryTimeStr) {
		this.otpExpiryTimeStr = otpExpiryTimeStr;
	}
	public String getMsgTxt() {
		return msgTxt;
	}
	public void setMsgTxt(String msgTxt) {
		this.msgTxt = msgTxt;
	}
	public String getIsFarmerMem() {
		return isFarmerMem;
	}
	public void setIsFarmerMem(String isFarmerMem) {
		this.isFarmerMem = isFarmerMem;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getOrgUserScope() {
		return orgUserScope;
	}
	public void setOrgUserScope(String orgUserScope) {
		this.orgUserScope = orgUserScope;
	}
	public String getOrgStateCode() {
		return orgStateCode;
	}
	public void setOrgStateCode(String orgStateCode) {
		this.orgStateCode = orgStateCode;
	}
	public String getOrgDistrictCode() {
		return orgDistrictCode;
	}
	public void setOrgDistrictCode(String orgDistrictCode) {
		this.orgDistrictCode = orgDistrictCode;
	}
	public String getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}
	public int getNoOfAttempt() {
		return noOfAttempt;
	}
	public void setNoOfAttempt(int noOfAttempt) {
		this.noOfAttempt = noOfAttempt;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public Timestamp getLockTimestamp() {
		return lockTimestamp;
	}
	public void setLockTimestamp(Timestamp lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}
	public String getIsOtpReq() {
		return isOtpReq;
	}

	public void setIsOtpReq(String isOtpReq) {
		this.isOtpReq = isOtpReq;
	}

	public String getIsLoginWithOtp() {
		return isLoginWithOtp;
	}

	public void setIsLoginWithOtp(String isLoginWithOtp) {
		this.isLoginWithOtp = isLoginWithOtp;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getThumbPrint() {
		return thumbPrint;
	}
	public void setThumbPrint(String thumbPrint) {
		this.thumbPrint = thumbPrint;
	}
	public String getOrgBaseMarket() {
		return orgBaseMarket;
	}
	public void setOrgBaseMarket(String orgBaseMarket) {
		this.orgBaseMarket = orgBaseMarket;
	}
	public String getOrgCategory() {
		return orgCategory;
	}
	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}
	
	
	
}