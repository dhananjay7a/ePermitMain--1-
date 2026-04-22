package com.epermit.register.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
	private String userId;
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
	
}

