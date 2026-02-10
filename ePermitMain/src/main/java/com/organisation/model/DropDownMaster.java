package com.organisation.model;

import java.io.Serializable;

public class DropDownMaster extends BaseModel implements Serializable, Cloneable {

	private String txnType; // dropdownName
	private String key; // dropdownId
	private String value; // dropdownValue
	private String description;
	private String mkt; // marketCode
	private String isActive;
	private String returnMsg;

	// For Role Mstr;

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	private String roleId;
	private String roleName;
	private String isAdmin;

	// For org_category_mstr

	private String orgCategoryScope;
	private String categoryType;

	private String response;

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getOrgCategoryScope() {
		return orgCategoryScope;
	}

	public void setOrgCategoryScope(String orgCategoryScope) {
		this.orgCategoryScope = orgCategoryScope;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMkt() {
		return mkt;
	}

	public void setMkt(String mkt) {
		this.mkt = mkt;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
}
