package com.organisation.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_id_generator")
public class IdGenerator implements Serializable{

//	private static final long serialVersionUID = 1L;
	
	@Id
	private String txnType;
	private BigDecimal id; 
	private String txnNo;
	
}
