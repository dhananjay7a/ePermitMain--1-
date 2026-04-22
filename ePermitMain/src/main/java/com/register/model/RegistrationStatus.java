package com.register.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_registration_status")
public class RegistrationStatus {
	// regStatus table : id(primary key), orgId , applicationStatus , actionTakenBy,
	// actionDateTime , Remarks

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orgId;
	private String applicationStatus;
	private String actionTakenBy;
	private LocalDateTime actionDateTime;
	private String remarks;

//	private String orgId;
//	private String orgRequestStatus;
//	private boolean idDigitallySigned;
//	private String isAutoApprove;
//	private LocalDateTime regSubmissionDate;
//	private LocalDateTime scrutinyApprovalDate;
//	private LocalDateTime secretaryApprovalDate;
//	private LocalDateTime renewalScrutinyApprovalDate;
//	private String scrutinyRemark;
//	private String secretaryRemark;
//	private String renewalScrutinyRemark;
//	private String renewalSecretaryRemark;
//	
//	private String isProfileUpdated;
//	private LocalDateTime profileUpdationDate;
//	private String profileUpdationStatus;

}
