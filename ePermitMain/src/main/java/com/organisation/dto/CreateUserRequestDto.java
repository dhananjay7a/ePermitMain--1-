package com.organisation.dto;

import lombok.Data;

@Data
public class CreateUserRequestDto {
	private String orgId;
    private String userId;
    private String userName;
    private String contactNumber;
    private String email;
    private String address;

    private String createdBy;
}
