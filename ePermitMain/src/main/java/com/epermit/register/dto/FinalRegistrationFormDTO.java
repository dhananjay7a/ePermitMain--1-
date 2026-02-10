package com.epermit.register.dto;

import com.organisation.model.RegisterAdditionalDetails;

import lombok.Data;

@Data
public class FinalRegistrationFormDTO {
	private BasicInfoDTO basicInfo;
	private PermanentAddressDTO permanentAddress; 
	private LicenseDetailsDTO licenseDetails;
	private BankGstDTO bankGst;
	private RegisterAdditionalDetails additional;
	private DocumentUploadDTO documentUpload;

}
