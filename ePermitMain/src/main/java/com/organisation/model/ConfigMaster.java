package com.organisation.model;

import lombok.Data;

@Data
public class ConfigMaster {
	
	private String conf_key;
	private String conf_value;
	private String remarks;
	private String created_by;
	private String created_on;
	private String modified_by;
	private String modified_on;
	private String returnMsg;
	private String category;
	private String purpose;
	

	
}

