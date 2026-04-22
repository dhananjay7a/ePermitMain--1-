package com.organisation.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_mst_market")
public class MarketMstr {

	@Id
	private String marketCode;

	private Timestamp createdOn;
	private String description;
	private String stateCode;
	private String districtCode;
	private String districtName;

//-------------------BANK DETAILS------------------------------	
	private String bankName;
	private String branchCode;
	private String ifscCode;
	private String bankAccNo;
	private String accountName;
	private String branchName;
	private String createdBy;

//-------------------------AUTHORIZED PERSON DETAILS------------------------------------
	private String authorizedPerson;
	private String telePhoneNo;
	private String address;
	private String city;
	private String pincode;
	private String emailId;
	private String mobileNo;

//-------------------------------------------------------------
	private String shortName;
	private String marketShortName1;
	private String marketShortName2;
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
	private String isSendSmsReqd;
	private String isMockMarket;
	private boolean restrictDenotifiedMkt;
	private String isDenotifiedMarket;
	private String modified_by;
	private String groupCode;
	private String dropdownValue;
	private String checkPostId;
	private String checkPostName;

	private String returnMsg;
	private String destinationType;
	private String isProcurementCenter;
	private String agencyCode;
	
	private String permitCnclStatus;
}
