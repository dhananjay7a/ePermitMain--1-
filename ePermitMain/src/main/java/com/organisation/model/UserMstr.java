package com.organisation.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_mst_user_details")
public class UserMstr {
	
	@Id
	private String userId;
	private String orgId;
	private String userName;
//	private String userCategory;
	
	private String userMobile;
	private String userEmail;
	private String firstLoginFlag;
	private String userPassword;
	private String userIsActive;
	
	private int noOfAttempt;
	private String isLock;
	private Timestamp lockTimestamp;
	
	private LocalDateTime createdOn;
	private String createdBy;
	@Column(columnDefinition = "TEXT")
	private String currentToken;  
}

