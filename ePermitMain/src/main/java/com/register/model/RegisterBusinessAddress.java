package com.register.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_business_address_temp")
public class RegisterBusinessAddress {

	@EmbeddedId
	private RegisterBusinessAddressId id;

	private String orgMarketAddress;
	private String orgMarketState;	
	private String orgMarketCity;
	private String orgMarketDist;
	private String orgMarketPoliceStation;
	private String orgMarketPostOffice;
	private String orgMarketPhoneNo;
	private String orgMarketPin;
	private String orgMarketBlockName;
	private LocalDateTime orgCreatedOn;
	private LocalDateTime orgModifiedOn;
	private String orgCreatedBy;
	private String orgModifiedBy;

}
