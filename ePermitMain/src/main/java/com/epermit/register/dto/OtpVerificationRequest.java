package com.epermit.register.dto;

import lombok.Data;

@Data
public class OtpVerificationRequest {
	private String mobileNo;
    private String otp;
}
