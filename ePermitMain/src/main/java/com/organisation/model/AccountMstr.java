package com.organisation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_mst_account_details")
public class AccountMstr {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

	private String partyCode;
    private String officeCode;
    private String virtualAccCode;
    private String bankAccNo;
    private String ifscCode;
    private String accountName;
    private LocalDateTime createdOn;
    private String createdBy;
}
