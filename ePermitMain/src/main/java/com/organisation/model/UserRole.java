package com.organisation.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_user_role")
public class UserRole {
	@Id
	private String roleId;
	private String orgCategory;
	
	private String roleName;
	private String isHost;
	private String createdBy;
	private LocalDateTime createdOn;
	private String modifiedBy;
	private LocalDateTime modifiedOn;
	
	
	

}
