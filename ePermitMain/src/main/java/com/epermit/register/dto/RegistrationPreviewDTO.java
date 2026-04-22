package com.epermit.register.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.register.model.LicenseeDetailsTemp;

import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegistrationPreviewDTO {
	

    // ====== Form Sections ======
    private BasicInfoDTO basicInfo;
    private PermanentAddressDTO permanentAddress;
    private List<RegisterBusinessAddressDTO> businessAddresses;
    private LicenseeDetailsTemp licenseDetails;
    private BankGstDTO bankGstDetails;
    private AdditionalDetailsDTO additionalDetails;
    private List<DocumentDTO> documents;
}
