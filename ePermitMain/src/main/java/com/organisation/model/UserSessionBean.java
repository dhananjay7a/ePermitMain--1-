package com.organisation.model;

import java.sql.Timestamp;
import java.util.List;

public class UserSessionBean {

	private String userId;
	private String orgId;
	private String orgName;
	private String userName;
	private String webSessionID;
	private String sessionID;
	private String lastRequestTime;
	private String isSessionExpired = "N";
	private String isLoggedIn;
	private String orgRole;
	private String maker;
	private String checker;
	private String userRoles;
	private String zoneId;
	private String branchZonalId;
	private String hierarchyId;
	private String returnMsg;
	private String userType;
	private String registered;
	private String remoteAddr;
	private String mobileNo;
	private String emailId;
	private String isFirstLogin;
	private String isActive;
	private boolean isHost;
	private boolean isUnified;
	private boolean isMember;
	private String renewalStatus; 
	private String requestStatus; 

	private String orgCategory;
	private String categoryType;
	private String orgUserScope;
	private String configCommodityKey;

	private List<String> roleList;
	private String orgBaseMarket;
	private String isMockMarket;
	private boolean isRmc;
	private int noOfAttempt;
	private String isExpired;
	private String isPartialExpired;
	private Timestamp registrationFeeValidity;
	private String isRegistrationFeePaid;
	private String isRenewalFeePaid;
	private Timestamp orgValidFrom;
	private String isNextYearLicencee;
	private String isOtpReq="N";
	private String isLoginWithOtp;
	private String otp;

	private String dataVisibility;
	
	//Uncomment for renewal changes, if necessary	
	private String marketYearApplied;
	
	public String getMarketYearApplied() {
		return marketYearApplied;
	}

	public void setMarketYearApplied(String marketYearApplied) {
		this.marketYearApplied = marketYearApplied;
	}

	public Timestamp getOrgValidFrom() {
		return orgValidFrom;
	}

	public void setOrgValidFrom(Timestamp orgValidFrom) {
		this.orgValidFrom = orgValidFrom;
	}

	public String getIsRegistrationFeePaid() {
		return isRegistrationFeePaid;
	}

	public void setIsRegistrationFeePaid(String isRegistrationFeePaid) {
		this.isRegistrationFeePaid = isRegistrationFeePaid;
	}

	public String getIsRenewalFeePaid() {
		return isRenewalFeePaid;
	}

	public void setIsRenewalFeePaid(String isRenewalFeePaid) {
		this.isRenewalFeePaid = isRenewalFeePaid;
	}

	public Timestamp getRegistrationFeeValidity() {
		return registrationFeeValidity;
	}

	public void setRegistrationFeeValidity(Timestamp timestamp) {
		this.registrationFeeValidity = timestamp;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}
	
	public String getIsPartialExpired() {
		return isPartialExpired;
	}

	public void setIsPartialExpired(String isPartialExpired) {
		this.isPartialExpired = isPartialExpired;
	}

	public String getWebSessionID() {
		return webSessionID;
	}

	public void setWebSessionID(String webSessionID) {
		this.webSessionID = webSessionID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(String lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}

	public String getIsSessionExpired() {
		return isSessionExpired;
	}

	public void setIsSessionExpired(String isSessionExpired) {
		this.isSessionExpired = isSessionExpired;
	}

	public String getIsLoggedIn() {
		return isLoggedIn;
	}

	public void setIsLoggedIn(String isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getOrgRole() {
		return orgRole;
	}

	public void setOrgRole(String orgRole) {
		this.orgRole = orgRole;
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

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getBranchZonalId() {
		return branchZonalId;
	}

	public void setBranchZonalId(String branchZonalId) {
		this.branchZonalId = branchZonalId;
	}

	public String getHierarchyId() {
		return hierarchyId;
	}

	public void setHierarchyId(String hierarchyId) {
		this.hierarchyId = hierarchyId;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRegistered() {
		return registered;
	}

	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getIsFirstLogin() {
		return isFirstLogin;
	}

	public void setIsFirstLogin(String isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}

	public String getOrgUserScope() {
		return orgUserScope;
	}

	public void setOrgUserScope(String orgUserScope) {
		this.orgUserScope = orgUserScope;
	}

	public String getConfigCommodityKey() {
		return configCommodityKey;
	}

	public void setConfigCommodityKey(String configCommodityKey) {
		this.configCommodityKey = configCommodityKey;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
	}

	public boolean isNeitherHostNorMember() {
		return !this.isHost && !this.isMember;
	}

	public String getRenewalStatus() {
		return renewalStatus;
	}

	public void setRenewalStatus(String renewalStatus) {
		this.renewalStatus = renewalStatus;
	}
	
	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getOrgBaseMarket() {
		return orgBaseMarket;
	}

	public void setOrgBaseMarket(String orgBaseMarket) {
		this.orgBaseMarket = orgBaseMarket;
	}

	public String getIsMockMarket() {
		return isMockMarket;
	}

	public void setIsMockMarket(String isMockMarket) {
		this.isMockMarket = isMockMarket;
	}

	public boolean isRmc() {
		return isRmc;
	}

	public void setRmc(boolean isRmc) {
		this.isRmc = isRmc;
	}

	public int getNoOfAttempt() {
		return noOfAttempt;
	}

	public void setNoOfAttempt(int noOfAttempt) {
		this.noOfAttempt = noOfAttempt;
	}

	public String getIsNextYearLicencee() {
		return isNextYearLicencee;
	}

	public void setIsNextYearLicencee(String isNextYearLicencee) {
		this.isNextYearLicencee = isNextYearLicencee;
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

	public String getDataVisibility() {
		return dataVisibility;
	}

	public void setDataVisibility(String dataVisibility) {
		this.dataVisibility = dataVisibility;
	}

	public boolean isUnified() {
		return isUnified;
	}

	public void setUnified(boolean isUnified) {
		this.isUnified = isUnified;
	}
	
	

}
