package com.organisation.model;

public class UserMapping extends BaseModel {
	private String 	userId;
	private String  isActive;
	private String userName;
	private String userCategory;
	private String orgId;
	private String createdBy;
	private java.sql.Timestamp createdOn ;
	private java.sql.Timestamp modifiedOn ;
	private String modifiedBy;
	private String returnMsg;
	private String orgUserScope;
	private String orgStateCode;
	private String orgDistrictCode;
	private String  marketCode;
	private String orgStateName;
	private String orgDistrictName;
	private boolean restrictDenotifiedMkt;
	private long errorCode;
	
	public long getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}
	public boolean isRestrictDenotifiedMkt() {
		return restrictDenotifiedMkt;
	}
	public void setRestrictDenotifiedMkt(boolean restrictDenotifiedMkt) {
		this.restrictDenotifiedMkt = restrictDenotifiedMkt;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMarketCode() {
		return marketCode;
	}
	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
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
	public String getOrgStateName() {
		return orgStateName;
	}
	public void setOrgStateName(String orgStateName) {
		this.orgStateName = orgStateName;
	}
	public String getOrgDistrictName() {
		return orgDistrictName;
	}
	public void setOrgDistrictName(String orgDistrictName) {
		this.orgDistrictName = orgDistrictName;
	}

	

}
