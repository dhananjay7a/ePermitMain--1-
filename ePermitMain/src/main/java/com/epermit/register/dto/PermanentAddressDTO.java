package com.epermit.register.dto;

import java.util.List;

import lombok.Data;

@Data
public class PermanentAddressDTO {
    private String orgId;
    private String orgApplicantName;
    private String orgApplicantParentName;
    private String orgAddress;
    private String orgState;
    private String orgCity;
    private String orgDist;
    private String orgPoliceStation;
    private String orgPostOffice;
    private String orgMobileNo;
    private String orgPin;
    private String orgBlockName;
    private String addressCreatedBy;
    
    private List<RegisterBusinessAddressDTO> businessAddresses;
}
