package com.organisation.dto;

import lombok.Data;

@Data
public class UserRoleResponseDto {
	   private String userId;
	    private String roleId;
	    private String roleName;
	    private String isActive;
	    
		public UserRoleResponseDto(String userId, String roleId, String roleName, String isActive) {
			super();
			this.userId = userId;
			this.roleId = roleId;
			this.roleName = roleName;
			this.isActive = isActive;
			
		}
	    
	    

}
