package com.epermit.register.dto;

import lombok.Data;

@Data
public class LicenseDetailsDTO {
    
    private String orgId;
    private String licenseNo;
    private String licenseBookNo;
    private String licenseReceiptBookNo;
    private String licenseReceiptNo;
    private String licenseFromDate;
    private String licenseToDate;
}

