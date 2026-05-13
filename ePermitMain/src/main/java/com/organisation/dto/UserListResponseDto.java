package com.organisation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponseDto {
	private String userId;
    private String userName;
    private String orgId;
    private String userEmail;
    private String userMobile;
    private String orgAddress;
    private String isActive;
	
    
    
}
