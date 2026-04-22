package com.organisation.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_user_role_mapping")
@IdClass(UserRoleMappingId.class)
public class UserRoleMapping {
	@Id
	private String userId;
	@Id
	private String roleId;
	private String isActive;
	private String createdBy;
	private LocalDateTime createdOn;
	private String modifiedBy;
	private LocalDateTime modifiedOn;
	
	

}
