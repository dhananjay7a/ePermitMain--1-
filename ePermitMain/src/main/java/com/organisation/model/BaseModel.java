package com.organisation.model;

import lombok.Data;

@Data
public abstract class BaseModel {
	private int startRow;	
	private int endRow;
	private int totalRows;
	private String whereCondition;
	private String orderBy;
	
}
