package com.epermit.register.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BasicInfoDTO {
    private String userId;
    private String orgId;
    private boolean unifiedLicense;
    private boolean hasExistingLicense;  // work on it later
    private String orgCategory;
    private String orgName;
    private String orgType;
    private Date orgConsttnDate;
    private String orgBaseMarket;
    private String orgDocType;
    private String orgDocNo;
    private String orgCreatedBy;
    private String orgModifiedBy;
    
    
}

