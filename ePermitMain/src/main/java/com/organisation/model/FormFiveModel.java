package com.organisation.model;


import java.sql.Timestamp;

import lombok.Data;

@Data
public class FormFiveModel {

	private String orgId;
	private int seqNo;
	private String storageName;
	private String villageName;
	private String jlNo;
	private String premisesNo;
	private String frequency;
	private String boundary;
	private String remarks;
	private String createdBy;
	private Timestamp createdOn;
	private String modifiedBy;
	private Timestamp modifiedOn;
	
}
