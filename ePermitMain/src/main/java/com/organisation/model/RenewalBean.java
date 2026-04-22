package com.organisation.model;


import java.sql.Timestamp;

public class RenewalBean {
	
	private String reqId;
	private String orgId;
	private Timestamp requestDate;
	private Timestamp requestStartDate;
	private Timestamp requestEndDate;
	private Timestamp requestApprovalStartDate;
	private Timestamp requestApprovalEndDate;
	private Timestamp createdOn;
	private String createdBy;
	private String renewalStatus;
	private String depositRefNo;
	private String modifiedBy;
	private String modifiedOn;
	private String marketYearApp;
	
	public String getMarketYearApp() {
		return marketYearApp;
	}
	public void setMarketYearApp(String marketYearApp) {
		this.marketYearApp = marketYearApp;
	}
	public String getDepositRefNo() {
		return depositRefNo;
	}
	public void setDepositRefNo(String depositRefNo) {
		this.depositRefNo = depositRefNo;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getRenewalStatus() {
		return renewalStatus;
	}
	public void setRenewalStatus(String renewalStatus) {
		this.renewalStatus = renewalStatus;
	}
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public Timestamp getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Timestamp requestDate) {
		this.requestDate = requestDate;
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
	public Timestamp getRequestApprovalStartDate() {
		return requestApprovalStartDate;
	}
	public void setRequestApprovalStartDate(Timestamp requestApprovalStartDate) {
		this.requestApprovalStartDate = requestApprovalStartDate;
	}
	public Timestamp getRequestApprovalEndDate() {
		return requestApprovalEndDate;
	}
	public void setRequestApprovalEndDate(Timestamp requestApprovalEndDate) {
		this.requestApprovalEndDate = requestApprovalEndDate;
	}
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
}
