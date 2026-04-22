package com.organisation.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
	private String userId;
	private String newPassword;
	private String confirmNewPassword;
}
