package com.organisation.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Table(name = "un_registration_mstr")
public class RegistrationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "ORG_ID")
	private String orgId;

	@Column(name = "ORG_NAME")
	private String orgName;

	@Column(name = "ORG_CATEGORY")
	private String orgCategory;

	@Column(name = "ORG_TYPE")
	private String orgType;

	@Column(name = "ORG_CNSTTTN_DATE")
	private Timestamp orgConsttnDate;

	@Column(name = "ORG_ADDRESS")
	private String orgAddress;

	@Column(name = "ORG_CITY")
	private String orgCity;

	@Column(name = "ORG_STATE")
	private String orgState;

	@Column(name = "ORG_DISTRICT")
	private String orgDistrict;

	@Column(name = "ORG_PIN")
	private String orgPinCode;

	@Column(name = "ORG_MOBILE_NO")
	private String orgMobileNo;

	@Column(name = "ORG_CONTACT_PERSON")
	private String orgContactPerson;

	@Column(name = "ORG_CONTACT_MOBILE")
	private String orgContactMobile;

	@Column(name = "ORG_CONTACT_EMAIL")
	private String orgContactEmail;

	@Column(name = "ORG_PERM_ADD")
	private String orgPermAddr;

	@Column(name = "ORG_PERM_CITY")
	private String orgPermCity;

	@Column(name = "ORG_PERM_STATE")
	private String orgPermState;

	@Column(name = "ORG_PERM_DIST")
	private String orgPermDist;

	@Column(name = "ORG_PERM_PIN")
	private String orgPermPin;

	@Column(name = "ORG_PANNO")
	private String orgPan;

	@Column(name = "ORG_ADHARNO")
	private String orgAdharNo;

	@Column(name = "ORG_GSTNO")
	private String orgGSTNo;

	@Column(name = "ORG_GST_STATE")
	private String orgGSTState;

	@Column(name = "ORG_BANK_ACC_NO")
	private String orgBackAccNo;

	@Column(name = "ORG_BANK_NAME")
	private String orgBankName;

	@Column(name = "ORG_BANK_BRANCH")
	private String orgBankBranch;

	@Column(name = "ORG_IFSC_CODE")
	private String orgIFSCCode;

	@Column(name = "ORG_REQUEST_NO")
	private String orgRequestNo;

	@Column(name = "ORG_REQUEST_STATUS")
	private String orgRequestStatus;

	@Column(name = "MIDDLE_NAME")
	private String middleName;

	@Column(name = "COMMODITY_CODE")
	private String commodityCode;

	@Column(name = "IS_REGISTERED")
	private String isRegistered;

	@Column(name = "EMPLOYEE_DETAILS")
	private String employeeDetails;

	@Column(name = "IS_BG_READY")
	private String isBGReady;

	@Column(name = "IS_GUILTY")
	private String isGuilty;

	@Column(name = "REGISTERED_WITH")
	private String registeredWith;

	@Column(name = "REGISTRATION_DETAILS")
	private String registrationDetails;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_PATH")
	private String docPath;

	@Column(name = "ORG_RENEWAL_STATUS")
	private String orgRenewalStatus;

	@Column(name = "IS_UNIFIED")
	private boolean unifiedLicense;

	@Column(name = "IS_AUTO_APPROVE")
	private String isAutoApprove;

	@Column(name = "PRIVATE_MARKET_YARD_AREA")
	private String privateMarketYardArea;

	@Column(name = "STYLE_FOR_PRIVATE_MARKET_YARD")
	private String styleForPrivateMarketYard;

	@Column(name = "SITUATION_PRIVATE_MARKET_YARD")
	private String situationPrivateMarketYard;

	@Column(name = "NATURE_OF_INTEREST_ON_LAND")
	private String natureOfInterestOnLand;

	@Column(name = "org_block_name ")
	private String orgBlockName;

	@Column(name = "org_perm_block_name  ")
	private String orgPermBlockName;

	/************************
	 * Additional fields added not present in DB
	 ****************************/

	private Timestamp orgValidFrom;
	private Date orgCreatedOn;
	private String orgCreatedBy;
	private Date orgModifiedOn;
	private String orgModifiedBy;
	private Date orgCnstDate;
	private String newPassword;
	private String password_c;
	private String ifscCode;
	private String mobileNo;
	private String emailId;
	private String isLoggedIn;
	private String password;
	private String returnMsg;
	private String fromDate;
	private String toDate;
	private String roleId;
	private String millCode;
	private String requestStatus;
	private String encryptedPassword;
	private Timestamp otpGenTime;
	private Timestamp otpExpiryTime;
	private String resendOTPPwd;
	private String requestNo;
	private String requestType;
	private String agencyCode;
	private String isOTP;
	private String isActive;
	private String isFreezed;
	private String firstLoginFlag;
	private String defaultMarket;
	private String crtUsrId;
	private Timestamp crtTm;
	private String userId;
	private String isNegInvAllowed;
	private String isMock;
	private String organizationType;

	private String otpGenTimeStr;
	private String otpExpiryTimeStr;

	private String orgVirAccNo;
	private String orgBankCode;

	private String editFlag;
	private String existMobile;
	private String isOwner;
	private String mobileCheck;
	private String isHost;

	private String scopeState;
	private String scopeDistrict;
	private String scopeMarket;

	private String scopeOrg;
	private String scopeUsr;

	private String orgCategoryScope;
	private String marketYearApp;  
	private String marketYr;
	private String impExp;

	private String marketDescription;

	private String depositRefNo;
	private String depositReqNo;
	private Timestamp requestDate;
	private Timestamp requestStartDate;
	private Timestamp requestEndDate;
	private Timestamp requestApprovalStartDate;
	private Timestamp requestApprovalEndDate;
	private String tempRenewalStatus;
	private String renewalRegStatus;
	private String licenseTerms;
	private String breachTerms;
	private Timestamp renewalApprovalFromDate;
	private Timestamp renewalApprovalToDate;
	private String approvedBy;
	private Timestamp approvedOn;
	private Long requestId;
	private Integer renewedCount;
	private String isRenewed;
	private Timestamp renewalDate;
	private String status;
	private String isUnified;
	private String showform3;

	private String resNo;

	/************* FOR CM MSTR & CT ACC MSTR ********************************/
	private String accountCode;
	private String virAccCode;
	private String accType;
	private String bankName;
	private String bankAccNo;
	private String branchName;
	private String settBnkCode;
	private String maxMarginLimit;
	private String accountName;
	private String partyCode;
	private String micrCode;
	private String flag;
	private String partyType;
	private String payOutType;
	private String branchCode;
	private String userDfltRole;
	private String isRegFeePaid;
	private Timestamp regFeeValidity;
	private String regFeeFlag;
	private String orgDocType;
	private String orgDocNo;
	private String orgBaseMarket;
	private String showForm3Button;
	private RegAdditionalInfo regAddtnInfo;
	private boolean termCondition;
	private boolean smsCondition;
	private boolean sms1Condition;
	private boolean actCondition;
	private String orgDocType1;
	private String orgDocType2;
	private String orgDocType3;
	private String orgDocType4;
	private String orgDocType5;
	private String orgDocType6;
	private String orgDocType7;
	private String orgDocType8;
	private String orgDocType9;
	private String orgDocType10;
	private String resendOTP;
	private String orgAdharNoTemp;
	private String docVisibility1;
	private String docVisibility2;
	private String docVisibility3;
	private String docVisibility4;
	private String docVisibility5;
	private String docVisibility6;
	private String docVisibility7;
	private String docVisibility8;
	private String docVisibility9;
	private String docVisibility10;
	private String isRenewed1;
	private String isRenewed2;
	private String iIsRenewed1;
	private String iIsRenewed2;
	private String iIsRenewed3;
	private String iIsRenewed4;
	private String iIsRenewed5;
	private String iIsRenewed6;
	private String iIsRenewed7;
	private String iIsRenewed8;
	private String iIsRenewed9;
	private String iIsRenewed10;

	public String getiIsRenewed6() {
		return iIsRenewed6;
	}

	public void setiIsRenewed6(String iIsRenewed6) {
		this.iIsRenewed6 = iIsRenewed6;
	}

	public String getiIsRenewed7() {
		return iIsRenewed7;
	}

	public void setiIsRenewed7(String iIsRenewed7) {
		this.iIsRenewed7 = iIsRenewed7;
	}

	public String getiIsRenewed8() {
		return iIsRenewed8;
	}

	public void setiIsRenewed8(String iIsRenewed8) {
		this.iIsRenewed8 = iIsRenewed8;
	}

	public String getiIsRenewed9() {
		return iIsRenewed9;
	}

	public void setiIsRenewed9(String iIsRenewed9) {
		this.iIsRenewed9 = iIsRenewed9;
	}

	public String getiIsRenewed10() {
		return iIsRenewed10;
	}

	public void setiIsRenewed10(String iIsRenewed10) {
		this.iIsRenewed10 = iIsRenewed10;
	}

	public String getiIsRenewed3() {
		return iIsRenewed3;
	}

	public void setiIsRenewed3(String iIsRenewed3) {
		this.iIsRenewed3 = iIsRenewed3;
	}

	public String getiIsRenewed4() {
		return iIsRenewed4;
	}

	public void setiIsRenewed4(String iIsRenewed4) {
		this.iIsRenewed4 = iIsRenewed4;
	}

	public String getiIsRenewed5() {
		return iIsRenewed5;
	}

	public void setiIsRenewed5(String iIsRenewed5) {
		this.iIsRenewed5 = iIsRenewed5;
	}

	public String getiIsRenewed1() {
		return iIsRenewed1;
	}

	public void setiIsRenewed1(String iIsRenewed1) {
		this.iIsRenewed1 = iIsRenewed1;
	}

	public String getiIsRenewed2() {
		return iIsRenewed2;
	}

	public void setiIsRenewed2(String iIsRenewed2) {
		this.iIsRenewed2 = iIsRenewed2;
	}

	public String getDocVisibility1() {
		return docVisibility1;
	}

	public void setDocVisibility1(String docVisibility1) {
		this.docVisibility1 = docVisibility1;
	}

	public String getDocVisibility2() {
		return docVisibility2;
	}

	public void setDocVisibility2(String docVisibility2) {
		this.docVisibility2 = docVisibility2;
	}

	public String getDocVisibility3() {
		return docVisibility3;
	}

	public void setDocVisibility3(String docVisibility3) {
		this.docVisibility3 = docVisibility3;
	}

	public String getDocVisibility4() {
		return docVisibility4;
	}

	public void setDocVisibility4(String docVisibility4) {
		this.docVisibility4 = docVisibility4;
	}

	public String getDocVisibility5() {
		return docVisibility5;
	}

	public void setDocVisibility5(String docVisibility5) {
		this.docVisibility5 = docVisibility5;
	}

	public String getDocVisibility6() {
		return docVisibility6;
	}

	public void setDocVisibility6(String docVisibility6) {
		this.docVisibility6 = docVisibility6;
	}

	public String getDocVisibility7() {
		return docVisibility7;
	}

	public void setDocVisibility7(String docVisibility7) {
		this.docVisibility7 = docVisibility7;
	}

	public String getDocVisibility8() {
		return docVisibility8;
	}

	public void setDocVisibility8(String docVisibility8) {
		this.docVisibility8 = docVisibility8;
	}

	public String getDocVisibility9() {
		return docVisibility9;
	}

	public void setDocVisibility9(String docVisibility9) {
		this.docVisibility9 = docVisibility9;
	}

	public String getDocVisibility10() {
		return docVisibility10;
	}

	public void setDocVisibility10(String docVisibility10) {
		this.docVisibility10 = docVisibility10;
	}

	public String getResendOTP() {
		return resendOTP;
	}

	public void setResendOTP(String resendOTP) {
		this.resendOTP = resendOTP;
	}

	public String getCopyRoToCa1() {
		return copyRoToCa1;
	}

	public void setCopyRoToCa1(String copyRoToCa1) {
		this.copyRoToCa1 = copyRoToCa1;
	}

	private String copyRoToCa1;

	public String getOrgDocType1() {
		return orgDocType1;
	}

	public void setOrgDocType1(String orgDocType1) {
		this.orgDocType1 = orgDocType1;
	}

	public String getOrgDocType2() {
		return orgDocType2;
	}

	public void setOrgDocType2(String orgDocType2) {
		this.orgDocType2 = orgDocType2;
	}

	public String getOrgDocType3() {
		return orgDocType3;
	}

	public void setOrgDocType3(String orgDocType3) {
		this.orgDocType3 = orgDocType3;
	}

	public String getOrgDocType4() {
		return orgDocType4;
	}

	public void setOrgDocType4(String orgDocType4) {
		this.orgDocType4 = orgDocType4;
	}

	public String getOrgDocType5() {
		return orgDocType5;
	}

	public void setOrgDocType5(String orgDocType5) {
		this.orgDocType5 = orgDocType5;
	}

	public String getOrgDocType6() {
		return orgDocType6;
	}

	public void setOrgDocType6(String orgDocType6) {
		this.orgDocType6 = orgDocType6;
	}

	public String getOrgDocType7() {
		return orgDocType7;
	}

	public void setOrgDocType7(String orgDocType7) {
		this.orgDocType7 = orgDocType7;
	}

	public String getOrgDocType8() {
		return orgDocType8;
	}

	public void setOrgDocType8(String orgDocType8) {
		this.orgDocType8 = orgDocType8;
	}

	public String getOrgDocType9() {
		return orgDocType9;
	}

	public void setOrgDocType9(String orgDocType9) {
		this.orgDocType9 = orgDocType9;
	}

	public String getOrgDocType10() {
		return orgDocType10;
	}

	public void setOrgDocType10(String orgDocType10) {
		this.orgDocType10 = orgDocType10;
	}

	public boolean isTermCondition() {
		return termCondition;
	}

	public boolean isSmsCondition() {
		return smsCondition;
	}

	public void setSmsCondition(boolean smsCondition) {
		this.smsCondition = smsCondition;
	}

	public boolean isSms1Condition() {
		return sms1Condition;
	}

	public void setSms1Condition(boolean sms1Condition) {
		this.sms1Condition = sms1Condition;
	}

	public boolean isActCondition() {
		return actCondition;
	}

	public void setActCondition(boolean actCondition) {
		this.actCondition = actCondition;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setTermCondition(boolean termCondition) {
		this.termCondition = termCondition;
	}

	private String regStatus;
	private String approverType;
	private String remarks;
	private String action;

	private String orgPoliceStation;
	private String orgPostOffice;

	private String prevExp;
	private String prevLicense;
	private String prevmarketLicense;
	private String GodownDetails;
	private String GuiltyDoc;
	private String impExpCommodity;
	private String modeType;
	private String applicantName;
	private String scrutinyRemark;
	private String secretaryRemark;
	private Timestamp prevValidity;

	/*********** For CashCollectionReceipt *************/

	private String crBookNo;
	private String crCreatedBy;
	private String crCreatedOn;
	private double crCreditAmount;
	private String crCreditAmountStr;
	private String crMarketCode;
	private String crPartyName;
	private String crRemarks;
	private String crPartyCode;
	private String crMarketName;
	private String crMarketAddress;

	/********** For Existing Licenses *******************/
	private boolean licenseExists;
	private String licenseno;
	private String licenseBookNo;
	private String licenseBookReceiptNo;
	private String licenseReceiptNo;

	private String regFeeReceiptNo;
	private double regFeeAmount;
	private String regType;
	private String regFeeBookType;
	private Timestamp regFeeReceiptDate;

	/********** For Pdf Money Receipt *******************/
	private double totalDebit;
	private String totalDebit1;
	private String traderLicenceeNo;
	private String txnRefNo;
	private String wbsambBankName;
	private String depositorBank;

	/********** For Digital Signature *******************/
	private boolean dSigned;

	/********** For Form 5 ******************************/
	Object[] form5details;
	private ArrayList<FormFiveModel> formFiveDetails;

	/********* Renewal Data ***************************/
	Object[] renewalBean;
	private RenewalBean renewalDetail;
	private String isExpired;
	private String isPartialExpired;
	private String isRenewal;
	private String isRenewalFeePaid;
	private String isNextYearLicencee;

	private String marketCode;

	private String isProfileUpdated;
	private String profileUpdateStatus;

//	/**************Registration Details************/
	// *************Renewal Registraion Document related Variable******************/
	private Timestamp createDateDocFilter;
	private boolean docVisibilityCheck;
	private String[] financialyear;
	private List<String> docVisibilityArray;

	/* *******************Added by SwaminiK on 09-11-2023**************** */
	private String regFeePaid;
	private String renFeePaid;

	/* *******************Added by SwaminiK on 30-11-2023**************** */
	private String UrConsignorMktCode;
	private String UrConsignorName;
	private String consignerName;
	private String consigneeName;
	private String UrConsignorAdd;
	private String UrConsignorContactNo;
	private String UrConsigneeMktCode;
	private String consignerMktCode;
	private String UrConsigneeName;
	private String consigneeMktCode;
	private String UrConsigneeAdd;
	private String UrConsigneeContactNo;
	private Timestamp NoticeIssuedDate;
	private String NoticeIssuedBy;
	private String isConsigneeConsignor;

	// Added by SwaminiK
	private String UrMarketCode;
	private String UrTraderName;
	private String permitNo;

	// ************* Registraion blockName related Variable******************/

	public String getOrgBlockName() {
		return orgBlockName;
	}

	public void setOrgBlockName(String orgBlockName) {
		this.orgBlockName = orgBlockName;
	}

	public String getOrgPermBlockName() {
		return orgPermBlockName;
	}

	public void setOrgPermBlockName(String orgPermBlockName) {
		this.orgPermBlockName = orgPermBlockName;
	}

	public String getIsNextYearLicencee() {
		return isNextYearLicencee;
	}

	public void setIsNextYearLicencee(String isNextYearLicencee) {
		this.isNextYearLicencee = isNextYearLicencee;
	}

	public String getIsRenewalFeePaid() {
		return isRenewalFeePaid;
	}

	public void setIsRenewalFeePaid(String isRenewalFeePaid) {
		this.isRenewalFeePaid = isRenewalFeePaid;
	}

	public String getIsRenewal() {
		return isRenewal;
	}

	public void setIsRenewal(String isRenewal) {
		this.isRenewal = isRenewal;
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

	public Object[] getRenewalBean() {
		return renewalBean;
	}

	public void setRenewalBean(Object[] renewalBean) {
		this.renewalBean = renewalBean;
	}

	public RenewalBean getRenewalDetail() {
		return renewalDetail;
	}

	public void setRenewalDetail(RenewalBean renewalDetail) {
		this.renewalDetail = renewalDetail;
	}

	public String getRenewalRegStatus() {
		return renewalRegStatus;
	}

	public void setRenewalRegStatus(String renewalRegStatus) {
		this.renewalRegStatus = renewalRegStatus;
	}

	public String getWbsambBankName() {
		return wbsambBankName;
	}

	public void setWbsambBankName(String wbsambBankName) {
		this.wbsambBankName = wbsambBankName;
	}

	public String getDepositorBank() {
		return depositorBank;
	}

	public void setDepositorBank(String depositorBank) {
		this.depositorBank = depositorBank;
	}

	public String getTxnRefNo() {
		return txnRefNo;
	}

	public void setTxnRefNo(String txnRefNo) {
		this.txnRefNo = txnRefNo;
	}

	public ArrayList<FormFiveModel> getFormFiveDetails() {
		return formFiveDetails;
	}

	public void setFormFiveDetails(ArrayList<FormFiveModel> formFiveDetails) {
		this.formFiveDetails = formFiveDetails;
	}

	public Object[] getForm5details() {
		return form5details;
	}

	public void setForm5details(Object[] form5details) {
		this.form5details = form5details;
	}

	public boolean isdSigned() {
		return dSigned;
	}

	public void setdSigned(boolean dSigned) {
		this.dSigned = dSigned;
	}

	public boolean getDSigned() {
		return dSigned;
	}

	public void setDSigned(boolean dSigned) {
		this.dSigned = dSigned;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getMarketDescription() {
		return marketDescription;
	}

	public void setMarketDescription(String marketDescription) {
		this.marketDescription = marketDescription;
	}

	public boolean isLicenseExists() {
		return licenseExists;
	}

	public void setLicenseExists(boolean licenseExists) {
		this.licenseExists = licenseExists;
	}

	public String getLicenseno() {
		return licenseno;
	}

	public void setLicenseno(String licenseno) {
		this.licenseno = licenseno;
	}

	public String getLicenseBookNo() {
		return licenseBookNo;
	}

	public void setLicenseBookNo(String licenseBookNo) {
		this.licenseBookNo = licenseBookNo;
	}

	public String getLicenseBookReceiptNo() {
		return licenseBookReceiptNo;
	}

	public void setLicenseBookReceiptNo(String licenseBookReceiptNo) {
		this.licenseBookReceiptNo = licenseBookReceiptNo;
	}

	public String getLicenseReceiptNo() {
		return licenseReceiptNo;
	}

	public void setLicenseReceiptNo(String licenseReceiptNo) {
		this.licenseReceiptNo = licenseReceiptNo;
	}

	public String getCrMarketAddress() {
		return crMarketAddress;
	}

	public void setCrMarketAddress(String crMarketAddress) {
		this.crMarketAddress = crMarketAddress;
	}

	public String getCrMarketName() {
		return crMarketName;
	}

	public void setCrMarketName(String crMarketName) {
		this.crMarketName = crMarketName;
	}

	public String getCrPartyCode() {
		return crPartyCode;
	}

	public void setCrPartyCode(String crPartyCode) {
		this.crPartyCode = crPartyCode;
	}

	public String getCrCreatedOn() {
		return crCreatedOn;
	}

	public void setCrCreatedOn(String crCreatedOn) {
		this.crCreatedOn = crCreatedOn;
	}

	public double getCrCreditAmount() {
		return crCreditAmount;
	}

	public void setCrCreditAmount(double crCreditAmount) {
		this.crCreditAmount = crCreditAmount;
	}

	public String getCrBookNo() {
		return crBookNo;
	}

	public void setCrBookNo(String crBookNo) {
		this.crBookNo = crBookNo;
	}

	public String getCrCreatedBy() {
		return crCreatedBy;
	}

	public void setCrCreatedBy(String crCreatedBy) {
		this.crCreatedBy = crCreatedBy;
	}

	public String getCrMarketCode() {
		return crMarketCode;
	}

	public void setCrMarketCode(String crMarketCode) {
		this.crMarketCode = crMarketCode;
	}

	public String getCrPartyName() {
		return crPartyName;
	}

	public void setCrPartyName(String crPartyName) {
		this.crPartyName = crPartyName;
	}

	public String getCrRemarks() {
		return crRemarks;
	}

	public void setCrRemarks(String crRemarks) {
		this.crRemarks = crRemarks;
	}

	public String getShowForm3Button() {
		return showForm3Button;
	}

	public void setShowForm3Button(String showForm3Button) {
		this.showForm3Button = showForm3Button;
	}

	public String getImpExpCommodity() {
		return impExpCommodity;
	}

	public void setImpExpCommodity(String impExpCommodity) {
		this.impExpCommodity = impExpCommodity;
	}

	public String getPrevExp() {
		return prevExp;
	}

	public void setPrevExp(String prevExp) {
		this.prevExp = prevExp;
	}

	public String getPrevLicense() {
		return prevLicense;
	}

	public void setPrevLicense(String prevLicense) {
		this.prevLicense = prevLicense;
	}

	public String getPrevmarketLicense() {
		return prevmarketLicense;
	}

	public void setPrevmarketLicense(String prevmarketLicense) {
		this.prevmarketLicense = prevmarketLicense;
	}

	public String getGodownDetails() {
		return GodownDetails;
	}

	public void setGodownDetails(String godownDetails) {
		GodownDetails = godownDetails;
	}

	public String getGuiltyDoc() {
		return GuiltyDoc;
	}

	public void setGuiltyDoc(String guiltyDoc) {
		GuiltyDoc = guiltyDoc;
	}

	public String getImpExp() {
		return impExp;
	}

	public void setImpExp(String impExp) {
		this.impExp = impExp;
	}

	public String getMarketYearApp() {
		return marketYearApp;
	}

	public void setMarketYearApp(String marketYearApp) {
		this.marketYearApp = marketYearApp;
	}

	public Timestamp getOrgValidFrom() {
		return orgValidFrom;
	}

	public void setOrgValidFrom(Timestamp orgValidFrom) {
		this.orgValidFrom = orgValidFrom;
	}

	public Date getOrgCnstDate() {
		return orgCnstDate;
	}

	public void setOrgCnstDate(Date orgCnstDate) {
		this.orgCnstDate = orgCnstDate;
	}

	public Date getOrgCreatedOn() {
		return orgCreatedOn;
	}

	public void setOrgCreatedOn(Date orgCreatedOn) {
		this.orgCreatedOn = orgCreatedOn;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}

	public String getApproverType() {
		return approverType;
	}

	public void setApproverType(String approverType) {
		this.approverType = approverType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public String getRegisteredWith() {
		return registeredWith;
	}

	public void setRegisteredWith(String registeredWith) {
		this.registeredWith = registeredWith;
	}

	public String getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(String registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public String getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(String isRegistered) {
		this.isRegistered = isRegistered;
	}

	public String getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(String employeeDetails) {
		this.employeeDetails = employeeDetails;
	}

	public String getIsBGReady() {
		return isBGReady;
	}

	public void setIsBGReady(String isBgReady) {
		this.isBGReady = isBgReady;
	}

	public String getIsGuilty() {
		return isGuilty;
	}

	public void setIsGuilty(String isGuilty) {
		this.isGuilty = isGuilty;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getVirAccCode() {
		return virAccCode;
	}

	public void setVirAccCode(String virAccCode) {
		this.virAccCode = virAccCode;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSettBnkCode() {
		return settBnkCode;
	}

	public void setSettBnkCode(String settBnkCode) {
		this.settBnkCode = settBnkCode;
	}

	public String getMaxMarginLimit() {
		return maxMarginLimit;
	}

	public void setMaxMarginLimit(String maxMarginLimit) {
		this.maxMarginLimit = maxMarginLimit;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPartyCode() {
		return partyCode;
	}

	public void setPartyCode(String partyCode) {
		this.partyCode = partyCode;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPartyType() {
		return partyType;
	}

	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}

	/**************************** CashBook **********/

	private String bankAcc;
	private String licenseeId;
	private String licenseeName;
	private String marketName;
	private String marketAddress;
	private String validFrom;
	private String validTill;
	private String brnNo;
	private String depositStatusT;
	private String licenseeAddress;

	/************************ getters & setters ****************************/
	public RegistrationMaster() {
	}

	public String getOrgId() {
		return this.orgId;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getPassword_c() {
		return password_c;
	}

	public void setPassword_c(String password_c) {
		this.password_c = password_c;
	}

	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}

	/*
	 * public String getMobileNo() { return mobileNo; }
	 * 
	 * public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
	 */
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getIsLoggedIn() {
		return isLoggedIn;
	}

	public void setIsLoggedIn(String isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
		organizationType = orgType;
	}

	public Timestamp getOrgConsttnDate() {
		return orgConsttnDate;
	}

	public void setOrgConsttnDate(Timestamp orgConsttnDate) {
		this.orgConsttnDate = orgConsttnDate;
	}

	public String getOrgAddress() {
		return orgAddress;
	}

	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}

	public String getOrgCity() {
		return orgCity;
	}

	public void setOrgCity(String orgCity) {
		this.orgCity = orgCity;
	}

	public String getOrgState() {
		return orgState;
	}

	public void setOrgState(String orgState) {
		this.orgState = orgState;
	}

	public String getOrgDistrict() {
		return orgDistrict;
	}

	public void setOrgDistrict(String orgDistrict) {
		this.orgDistrict = orgDistrict;
	}

	public String getOrgPinCode() {
		return orgPinCode;
	}

	public void setOrgPinCode(String orgPinCode) {
		this.orgPinCode = orgPinCode;
	}

	public String getOrgMobileNo() {
		return orgMobileNo;
	}

	public void setOrgMobileNo(String orgMobileNo) {
		this.orgMobileNo = orgMobileNo;
	}

	public String getOrgContactPerson() {
		return orgContactPerson;
	}

	public void setOrgContactPerson(String orgContactPerson) {
		this.orgContactPerson = orgContactPerson;
	}

	public String getOrgContactMobile() {
		return orgContactMobile;
	}

	public void setOrgContactMobile(String orgContactMobile) {
		this.orgContactMobile = orgContactMobile;
	}

	public String getOrgContactEmail() {
		return orgContactEmail;
	}

	public void setOrgContactEmail(String orgContactEmail) {
		this.orgContactEmail = orgContactEmail;
	}

	public String getOrgPermAddr() {
		return orgPermAddr;
	}

	public void setOrgPermAddr(String orgPermAddr) {
		this.orgPermAddr = orgPermAddr;
	}

	public String getOrgPermCity() {
		return orgPermCity;
	}

	public void setOrgPermCity(String orgPermCity) {
		this.orgPermCity = orgPermCity;
	}

	public String getOrgPermState() {
		return orgPermState;
	}

	public void setOrgPermState(String orgPermState) {
		this.orgPermState = orgPermState;
	}

	public String getOrgPermDist() {
		return orgPermDist;
	}

	public void setOrgPermDist(String orgPermDist) {
		this.orgPermDist = orgPermDist;
	}

	public String getOrgPermPin() {
		return orgPermPin;
	}

	public void setOrgPermPin(String orgPermPin) {
		this.orgPermPin = orgPermPin;
	}

	public String getOrgPan() {
		return orgPan;
	}

	public void setOrgPan(String orgPan) {
		this.orgPan = orgPan;
	}

	public String getOrgAdharNo() {
		return orgAdharNo;
	}

	public void setOrgAdharNo(String orgAdharNo) {
		this.orgAdharNo = orgAdharNo;
	}

	public String getOrgGSTNo() {
		return orgGSTNo;
	}

	public void setOrgGSTNo(String orgGSTNo) {
		this.orgGSTNo = orgGSTNo;
	}

	public String getOrgGSTState() {
		return orgGSTState;
	}

	public void setOrgGSTState(String orgGSTState) {
		this.orgGSTState = orgGSTState;
	}

	public String getOrgBackAccNo() {
		return orgBackAccNo;
	}

	public void setOrgBackAccNo(String orgBackAccNo) {
		this.orgBackAccNo = orgBackAccNo;
	}

	public String getOrgBankName() {
		return orgBankName;
	}

	public void setOrgBankName(String orgBankName) {
		this.orgBankName = orgBankName;
	}

	public String getOrgBankBranch() {
		return orgBankBranch;
	}

	public void setOrgBankBranch(String orgBankBranch) {
		this.orgBankBranch = orgBankBranch;
	}

	public String getOrgIFSCCode() {
		return orgIFSCCode;
	}

	public void setOrgIFSCCode(String orgIFSCCode) {
		this.orgIFSCCode = orgIFSCCode;
	}

	public String getOrgRequestNo() {
		return orgRequestNo;
	}

	public void setOrgRequestNo(String orgRequestNo) {
		this.orgRequestNo = orgRequestNo;
	}

	public String getOrgRequestStatus() {
		return orgRequestStatus;
	}

	public void setOrgRequestStatus(String orgRequestStatus) {
		this.orgRequestStatus = orgRequestStatus;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Timestamp getOtpGenTime() {
		return otpGenTime;
	}

	public void setOtpGenTime(Timestamp otpGenTime) {
		this.otpGenTime = otpGenTime;
	}

	public Timestamp getOtpExpiryTime() {
		return otpExpiryTime;
	}

	public void setOtpExpiryTime(Timestamp otpExpiryTime) {
		this.otpExpiryTime = otpExpiryTime;
	}

	public String getResendOTPPwd() {
		return resendOTPPwd;
	}

	public void setResendOTPPwd(String resendOTPPwd) {
		this.resendOTPPwd = resendOTPPwd;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getIsOTP() {
		return isOTP;
	}

	public void setIsOTP(String isOTP) {
		this.isOTP = isOTP;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getFirstLoginFlag() {
		return firstLoginFlag;
	}

	public void setFirstLoginFlag(String firstLoginFlag) {
		this.firstLoginFlag = firstLoginFlag;
	}

	public String getDefaultMarket() {
		return defaultMarket;
	}

	public void setDefaultMarket(String defaultMarket) {
		this.defaultMarket = defaultMarket;
	}

	public String getCrtUsrId() {
		return crtUsrId;
	}

	public void setCrtUsrId(String crtUsrId) {
		this.crtUsrId = crtUsrId;
	}

	public Timestamp getCrtTm() {
		return crtTm;
	}

	public void setCrtTm(Timestamp crtTm) {
		this.crtTm = crtTm;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIsNegInvAllowed() {
		return isNegInvAllowed;
	}

	public void setIsNegInvAllowed(String isNegInvAllowed) {
		this.isNegInvAllowed = isNegInvAllowed;
	}

	public String getIsMock() {
		return isMock;
	}

	public void setIsMock(String isMock) {
		this.isMock = isMock;
	}

	public String getIsFreezed() {
		return isFreezed;
	}

	public void setIsFreezed(String isFreezed) {
		this.isFreezed = isFreezed;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getOtpGenTimeStr() {
		return otpGenTimeStr;
	}

	public void setOtpGenTimeStr(String otpGenTimeStr) {
		this.otpGenTimeStr = otpGenTimeStr;
	}

	public String getOtpExpiryTimeStr() {
		return otpExpiryTimeStr;
	}

	public void setOtpExpiryTimeStr(String otpExpiryTimeStr) {
		this.otpExpiryTimeStr = otpExpiryTimeStr;
	}

	public String getPayOutType() {
		return payOutType;
	}

	public void setPayOutType(String payOutType) {
		this.payOutType = payOutType;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getOrgVirAccNo() {
		return orgVirAccNo;
	}

	public void setOrgVirAccNo(String orgVirAccNo) {
		this.orgVirAccNo = orgVirAccNo;
	}

	public String getOrgBankCode() {
		return orgBankCode;
	}

	public void setOrgBankCode(String orgBankCode) {
		this.orgBankCode = orgBankCode;
	}

	public String getEditFlag() {
		return editFlag;
	}

	public void setEditFlag(String editFlag) {
		this.editFlag = editFlag;
	}

	public String getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(String isOwner) {
		this.isOwner = isOwner;
	}

	public String getExistMobile() {
		return existMobile;
	}

	public void setExistMobile(String existMobile) {
		this.existMobile = existMobile;
	}

	public String getMobileCheck() {
		return mobileCheck;
	}

	public void setMobileCheck(String mobileCheck) {
		this.mobileCheck = mobileCheck;
	}

	public String getIsHost() {
		return isHost;
	}

	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}

	public String getScopeState() {
		return scopeState;
	}

	public void setScopeState(String scopeState) {
		this.scopeState = scopeState;
	}

	public String getScopeDistrict() {
		return scopeDistrict;
	}

	public void setScopeDistrict(String scopeDistrict) {
		this.scopeDistrict = scopeDistrict;
	}

	public String getScopeMarket() {
		return scopeMarket;
	}

	public void setScopeMarket(String scopeMarket) {
		this.scopeMarket = scopeMarket;
	}

	public String getScopeOrg() {
		return scopeOrg;
	}

	public void setScopeOrg(String scopeOrg) {
		this.scopeOrg = scopeOrg;
	}

	public String getScopeUsr() {
		return scopeUsr;
	}

	public void setScopeUsr(String scopeUsr) {
		this.scopeUsr = scopeUsr;
	}

	public String getOrgCategoryScope() {
		return orgCategoryScope;
	}

	public void setOrgCategoryScope(String orgCategoryScope) {
		this.orgCategoryScope = orgCategoryScope;
	}

	public String getUserDfltRole() {
		return userDfltRole;
	}

	public void setUserDfltRole(String userDfltRole) {
		this.userDfltRole = userDfltRole;
	}

	public String getIsRegFeePaid() {
		return isRegFeePaid;
	}

	public void setIsRegFeePaid(String isRegFeePaid) {
		this.isRegFeePaid = isRegFeePaid;
	}

	public Timestamp getRegFeeValidity() {
		return regFeeValidity;
	}

	public void setRegFeeValidity(Timestamp regFeeValidity) {
		this.regFeeValidity = regFeeValidity;
	}

	public String getRegFeeFlag() {
		return regFeeFlag;
	}

	public void setRegFeeFlag(String regFeeFlag) {
		this.regFeeFlag = regFeeFlag;
	}

	public RegAdditionalInfo getRegAddtnInfo() {
		return regAddtnInfo;
	}

	public void setRegAddtnInfo(RegAdditionalInfo regAddtnInfo) {
		this.regAddtnInfo = regAddtnInfo;
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

	public String getOrgBaseMarket() {
		return orgBaseMarket;
	}

	public void setOrgBaseMarket(String orgBaseMarket) {
		this.orgBaseMarket = orgBaseMarket;
	}

	public String getOrgCreatedBy() {
		return orgCreatedBy;
	}

	public void setOrgCreatedBy(String orgCreatedBy) {
		this.orgCreatedBy = orgCreatedBy;
	}

	public Date getOrgModifiedOn() {
		return orgModifiedOn;
	}

	public void setOrgModifiedOn(Date orgModifiedOn) {
		this.orgModifiedOn = orgModifiedOn;
	}

	public String getOrgModifiedBy() {
		return orgModifiedBy;
	}

	public void setOrgModifiedBy(String orgModifiedBy) {
		this.orgModifiedBy = orgModifiedBy;
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

	public String getOrgRenewalStatus() {
		return orgRenewalStatus;
	}

	public void setOrgRenewalStatus(String orgRenewalStatus) {
		this.orgRenewalStatus = orgRenewalStatus;
	}

	public boolean isUnifiedLicense() {
		return unifiedLicense;
	}

	public void setUnifiedLicense(boolean unifiedLicense) {
		this.unifiedLicense = unifiedLicense;
	}

	public String getRegFeeReceiptNo() {
		return regFeeReceiptNo;
	}

	public void setRegFeeReceiptNo(String regFeeReceiptNo) {
		this.regFeeReceiptNo = regFeeReceiptNo;
	}

	public double getRegFeeAmount() {
		return regFeeAmount;
	}

	public void setRegFeeAmount(double regFeeAmount) {
		this.regFeeAmount = regFeeAmount;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getRegFeeBookType() {
		return regFeeBookType;
	}

	public void setRegFeeBookType(String regFeeBookType) {
		this.regFeeBookType = regFeeBookType;
	}

	public Timestamp getRegFeeReceiptDate() {
		return regFeeReceiptDate;
	}

	public void setRegFeeReceiptDate(Timestamp regFeeReceiptDate) {
		this.regFeeReceiptDate = regFeeReceiptDate;
	}

	public double getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(double totalDebit) {
		this.totalDebit = totalDebit;
	}

	public String getTraderLicenceeNo() {
		return traderLicenceeNo;
	}

	public void setTraderLicenceeNo(String traderLicenceeNo) {
		this.traderLicenceeNo = traderLicenceeNo;
	}

	public String getDepositRefNo() {
		return depositRefNo;
	}

	public void setDepositRefNo(String depositRefNo) {
		this.depositRefNo = depositRefNo;
	}

	public String getDepositReqNo() {
		return depositReqNo;
	}

	public void setDepositReqNo(String depositReqNo) {
		this.depositReqNo = depositReqNo;
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

	public String getTempRenewalStatus() {
		return tempRenewalStatus;
	}

	public void setTempRenewalStatus(String tempRenewalStatus) {
		this.tempRenewalStatus = tempRenewalStatus;
	}

	public String getModeType() {
		return modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getLicenseTerms() {
		return licenseTerms;
	}

	public void setLicenseTerms(String licenseTerms) {
		this.licenseTerms = licenseTerms;
	}

	public String getBreachTerms() {
		return breachTerms;
	}

	public void setBreachTerms(String breachTerms) {
		this.breachTerms = breachTerms;
	}

	public Timestamp getRenewalApprovalFromDate() {
		return renewalApprovalFromDate;
	}

	public void setRenewalApprovalFromDate(Timestamp renewalApprovalFromDate) {
		this.renewalApprovalFromDate = renewalApprovalFromDate;
	}

	public Timestamp getRenewalApprovalToDate() {
		return renewalApprovalToDate;
	}

	public void setRenewalApprovalToDate(Timestamp renewalApprovalToDate) {
		this.renewalApprovalToDate = renewalApprovalToDate;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Integer getRenewedCount() {
		return renewedCount;
	}

	public void setRenewedCount(Integer renewedCount) {
		this.renewedCount = renewedCount;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getAgencyCode() {
		return agencyCode;
	}

	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}

	public String getMillCode() {
		return millCode;
	}

	public void setMillCode(String millCode) {
		this.millCode = millCode;
	}

	public String getIsAutoApprove() {
		return isAutoApprove;
	}

	public void setIsAutoApprove(String isAutoApprove) {
		this.isAutoApprove = isAutoApprove;
	}

	public String getPrivateMarketYardArea() {
		return privateMarketYardArea;
	}

	public void setPrivateMarketYardArea(String privateMarketYardArea) {
		this.privateMarketYardArea = privateMarketYardArea;
	}

	public String getStyleForPrivateMarketYard() {
		return styleForPrivateMarketYard;
	}

	public void setStyleForPrivateMarketYard(String styleForPrivateMarketYard) {
		this.styleForPrivateMarketYard = styleForPrivateMarketYard;
	}

	public String getSituationPrivateMarketYard() {
		return situationPrivateMarketYard;
	}

	public void setSituationPrivateMarketYard(String situationPrivateMarketYard) {
		this.situationPrivateMarketYard = situationPrivateMarketYard;
	}

	public String getNatureOfInterestOnLand() {
		return natureOfInterestOnLand;
	}

	public void setNatureOfInterestOnLand(String natureOfInterestOnLand) {
		this.natureOfInterestOnLand = natureOfInterestOnLand;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getIsUnified() {
		return isUnified;
	}

	public void setIsUnified(String isUnified) {
		this.isUnified = isUnified;
	}

	public String getShowform3() {
		return showform3;
	}

	public void setShowform3(String showform3) {
		this.showform3 = showform3;
	}

	public String getResNo() {
		return resNo;
	}

	public void setResNo(String resNo) {
		this.resNo = resNo;
	}

	public String getCrCreditAmountStr() {
		return crCreditAmountStr;
	}

	public void setCrCreditAmountStr(String crCreditAmountStr) {
		this.crCreditAmountStr = crCreditAmountStr;
	}

	public String getIsProfileUpdated() {
		return isProfileUpdated;
	}

	public void setIsProfileUpdated(String isProfileUpdated) {
		this.isProfileUpdated = isProfileUpdated;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getBankAcc() {
		return bankAcc;
	}

	public void setBankAcc(String bankAcc) {
		this.bankAcc = bankAcc;
	}

	public String getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(String licenseeId) {
		this.licenseeId = licenseeId;
	}

	public String getLicenseeName() {
		return licenseeName;
	}

	public void setLicenseeName(String licenseeName) {
		this.licenseeName = licenseeName;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketAddress() {
		return marketAddress;
	}

	public void setMarketAddress(String marketAddress) {
		this.marketAddress = marketAddress;
	}

	public String getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	public String getValidTill() {
		return validTill;
	}

	public void setValidTill(String validTill) {
		this.validTill = validTill;
	}

	public String getBrnNo() {
		return brnNo;
	}

	public void setBrnNo(String brnNo) {
		this.brnNo = brnNo;
	}

	public String getLicenseeAddress() {
		return licenseeAddress;
	}

	public void setLicenseeAddress(String licenseeAddress) {
		this.licenseeAddress = licenseeAddress;
	}

	public String getDepositStatusT() {
		return depositStatusT;
	}

	public void setDepositStatusT(String depositStatusT) {
		this.depositStatusT = depositStatusT;
	}

	public String getMarketCode() {
		return marketCode;
	}

	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}

	public String getTotalDebit1() {
		return totalDebit1;
	}

	public void setTotalDebit1(String totalDebit1) {
		this.totalDebit1 = totalDebit1;
	}

	public Timestamp getCreateDateDocFilter() {
		return createDateDocFilter;
	}

	public void setCreateDateDocFilter(Timestamp createDateDocFilter) {
		this.createDateDocFilter = createDateDocFilter;
	}

	public boolean isDocVisibilityCheck() {
		return docVisibilityCheck;
	}

	public void setDocVisibilityCheck(boolean docVisibilityCheck) {
		this.docVisibilityCheck = docVisibilityCheck;
	}

	public String[] getFinancialyear() {
		return financialyear;
	}

	public void setFinancialyear(String[] financialyear) {
		this.financialyear = financialyear;
	}

	public List<String> getDocVisibilityArray() {
		return docVisibilityArray;
	}

	public void setDocVisibilityArray(List<String> docVisibilityArray) {
		this.docVisibilityArray = docVisibilityArray;
	}

	public String getScrutinyRemark() {
		return scrutinyRemark;
	}

	public void setScrutinyRemark(String scrutinyRemark) {
		this.scrutinyRemark = scrutinyRemark;
	}

	public String getSecretaryRemark() {
		return secretaryRemark;
	}

	public void setSecretaryRemark(String secretaryRemark) {
		this.secretaryRemark = secretaryRemark;
	}

	public String getRegFeePaid() {
		return regFeePaid;
	}

	public void setRegFeePaid(String regFeePaid) {
		this.regFeePaid = regFeePaid;
	}

	public String getUrConsignorMktCode() {
		return UrConsignorMktCode;
	}

	public void setUrConsignorMktCode(String urConsignorMktCode) {
		UrConsignorMktCode = urConsignorMktCode;
	}

	public String getUrConsignorName() {
		return UrConsignorName;
	}

	public void setUrConsignorName(String urConsignorName) {
		UrConsignorName = urConsignorName;
	}

	public String getUrConsignorAdd() {
		return UrConsignorAdd;
	}

	public void setUrConsignorAdd(String urConsignorAdd) {
		UrConsignorAdd = urConsignorAdd;
	}

	public String getUrConsignorContactNo() {
		return UrConsignorContactNo;
	}

	public void setUrConsignorContactNo(String urConsignorContactNo) {
		UrConsignorContactNo = urConsignorContactNo;
	}

	public String getUrConsigneeName() {
		return UrConsigneeName;
	}

	public void setUrConsigneeName(String urConsigneeName) {
		UrConsigneeName = urConsigneeName;
	}

	public String getUrConsigneeAdd() {
		return UrConsigneeAdd;
	}

	public void setUrConsigneeAdd(String urConsigneeAdd) {
		UrConsigneeAdd = urConsigneeAdd;
	}

	public String getUrConsigneeContactNo() {
		return UrConsigneeContactNo;
	}

	public void setUrConsigneeContactNo(String urConsigneeContactNo) {
		UrConsigneeContactNo = urConsigneeContactNo;
	}

	public String getUrConsigneeMktCode() {
		return UrConsigneeMktCode;
	}

	public void setUrConsigneeMktCode(String urConsigneeMktCode) {
		UrConsigneeMktCode = urConsigneeMktCode;
	}

	public Timestamp getNoticeIssuedDate() {
		return NoticeIssuedDate;
	}

	public void setNoticeIssuedDate(Timestamp noticeIssuedDate) {
		NoticeIssuedDate = noticeIssuedDate;
	}

	public String getNoticeIssuedBy() {
		return NoticeIssuedBy;
	}

	public void setNoticeIssuedBy(String noticeIssuedBy) {
		NoticeIssuedBy = noticeIssuedBy;
	}

	public String getIsConsigneeConsignor() {
		return isConsigneeConsignor;
	}

	public void setIsConsigneeConsignor(String isConsigneeConsignor) {
		this.isConsigneeConsignor = isConsigneeConsignor;
	}

	public String getUrMarketCode() {
		return UrMarketCode;
	}

	public void setUrMarketCode(String urMarketCode) {
		UrMarketCode = urMarketCode;
	}

	public String getUrTraderName() {
		return UrTraderName;
	}

	public void setUrTraderName(String urTraderName) {
		UrTraderName = urTraderName;
	}

	public String getConsignerName() {
		return consignerName;
	}

	public void setConsignerName(String consignerName) {
		this.consignerName = consignerName;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public String getConsignerMktCode() {
		return consignerMktCode;
	}

	public void setConsignerMktCode(String consignerMktCode) {
		this.consignerMktCode = consignerMktCode;
	}

	public String getConsigneeMktCode() {
		return consigneeMktCode;
	}

	public void setConsigneeMktCode(String consigneeMktCode) {
		this.consigneeMktCode = consigneeMktCode;
	}

	public String getPermitNo() {
		return permitNo;
	}

	public void setPermitNo(String permitNo) {
		this.permitNo = permitNo;
	}

	public String getRenFeePaid() {
		return renFeePaid;
	}

	public void setRenFeePaid(String renFeePaid) {
		this.renFeePaid = renFeePaid;
	}

	public Timestamp getPrevValidity() {
		return prevValidity;
	}

	public void setPrevValidity(Timestamp prevValidity) {
		this.prevValidity = prevValidity;
	}

	public String getProfileUpdateStatus() {
		return profileUpdateStatus;
	}

	public void setProfileUpdateStatus(String profileUpdateStatus) {
		this.profileUpdateStatus = profileUpdateStatus;
	}

	public String getOrgAdharNoTemp() {
		return orgAdharNoTemp;
	}

	public void setOrgAdharNoTemp(String orgAdharNoTemp) {
		this.orgAdharNoTemp = orgAdharNoTemp;
	}

	public String getIsRenewed1() {
		return isRenewed1;
	}

	public void setIsRenewed1(String isRenewed1) {
		this.isRenewed1 = isRenewed1;
	}

	public String getIsRenewed2() {
		return isRenewed2;
	}

	public void setIsRenewed2(String isRenewed2) {
		this.isRenewed2 = isRenewed2;
	}

	public String getMarketYr() {
		return marketYr;
	}

	public void setMarketYr(String marketYr) {
		this.marketYr = marketYr;
	}

}