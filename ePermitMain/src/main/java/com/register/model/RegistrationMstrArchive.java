package com.register.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_registration_mstr_archive")
public class RegistrationMstrArchive {

    @Id
    @GeneratedValue
    private Long id;
    
    private String orgId; 
    private String archiveCreatedBy;
    private LocalDateTime archiveCreatedOn;

    private String orgName;
    private String orgName1;
    private String orgName2;
    private String orgCategory;
    private String orgType;

    private LocalDate orgCnstttnDate;

    private String orgAddress;
    private String orgCity;
    private String orgStateCode;
    private String orgDistrictCode;
    private String orgPin;

    private String orgPhoneno;
    private String orgContactPerson;
    private String orgContactMobile;
    private String orgContactEmail;

    private String orgPermAdd;
    private String orgPermCity;
    private String orgPermState;
    private String orgPermDist;
    private String orgPermPin;

    private String orgPanno;
    private String orgAdharno;
    private String orgGstno;
    private String orgGstState;

    private String orgBankAccNo;
    private String orgBankName;
    private String orgBankBranch;
    private String orgIfscCode;

    private String orgFirstLogin;
    private String orgPassword;
    private String orgEncPassword;
    private String orgIsActive;
    private String orgIsMock;

    private String orgRequestNo;
    private String orgRequestStatus;

    private LocalDateTime orgOtpGenerationTime;
    private LocalDateTime orgOtpExpiryTime;

    private String orgIsOtp;
    private String orgFirstLoginFlag;
    private String orgIsNegInvtAllowed;

    private String orgBankCode;
    private String orgVirAccNo;
    private String orgCategoryScope;

    private String orgCreatedBy;
    private LocalDateTime orgCreatedOn;
    private String orgModifiedBy;
    private LocalDateTime orgModifiedOn;

    private String isRegistrationFeePaid;
    private LocalDateTime registrationFeeValidity;

    private String orgDocType;
    private String orgDocNo;
    private String orgBaseMarket;

    private LocalDateTime orgValidFrom;

    private String orgPoliceStation;
    private String orgPostOffice;

    private Boolean isLicenseExist;

    private String licenseNo;
    private String licenseBookNo;
    private String licenseBookReceiptNo;
    private String licenseReceiptNo;

    private String orgRenewalStatus;

    private String applicantName;

    private Boolean isUnified;
    private Boolean isDigitallySigned;

    private LocalDateTime requestStartDate;
    private LocalDateTime requestEndDate;

    private String lastDepositRefNo;
    private String marketYearApplied;

    private String isRenewalFeePaid;

    private String licenseTerms;
    private String breachTerms;

    private String isRenewed;
    private LocalDateTime renewalDate;

    private BigDecimal renewedCount;
    private String renewedBy;

    private String currentRequestId;

    private String isAutoApprove;

    private String resolutionNo;

    private LocalDateTime registrationSubmissionDate;
    private LocalDateTime scrutinyApprovalDate;
    private LocalDateTime secretaryApprovalDate;

    private LocalDateTime renewalRequestDate;
    private LocalDateTime renewalScutinyApprovalDate;

    private String orgBlockName;
    private String orgPermBlockName;

    private String scrutinyRemark;
    private String secretaryRemark;

    private String renewalScrutinyRemark;
    private String renewalSecretaryRemark;

    private String isProfileUpdated;
    private LocalDateTime profileUpdationDate;
    private String profileUpdationStatus;
}
