package com.organisation.model;

import java.time.LocalDateTime;

import com.register.model.RegisterBusinessAddressId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_business_address")
public class RegisterBusinessAddressDetails {
	
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
