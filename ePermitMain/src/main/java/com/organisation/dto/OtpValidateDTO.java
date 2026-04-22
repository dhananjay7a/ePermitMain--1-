package com.organisation.dto;

import lombok.Data;

@Data
public class OtpValidateDTO {
    private String userId;
    private String otp;
}
