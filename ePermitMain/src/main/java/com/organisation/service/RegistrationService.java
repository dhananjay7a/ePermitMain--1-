package com.organisation.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.epermit.register.dto.AdditionalDetailsDTO;
import com.epermit.register.dto.BankGstDTO;
import com.epermit.register.dto.BasicInfoDTO;
import com.epermit.register.dto.ChangePasswordDTO;
import com.epermit.register.dto.FinalRegistrationFormDTO;
import com.epermit.register.dto.PermanentAddressDTO;
import com.epermit.register.dto.RegistrationPreviewDTO;
import com.epermit.register.dto.ScrutinyRequestDTO;
import com.epermit.register.dto.TermsAndConditionsResponseDTO;
import com.epermit.register.dto.UserDigiSignRequestDto;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.dto.ChangeDefaultPasswordDTO;
import com.organisation.dto.OtpValidateDTO;
import com.organisation.model.DropDownMaster;
import com.organisation.model.MarketMstr;
import com.organisation.model.MessageTracker;
import com.organisation.model.OrgCategoryMaster;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.ResponseMessage;
import com.organisation.model.UserMstr;
import com.register.model.BankGstDetails;
import com.register.model.DocumentUploadTemp;
import com.register.model.LicenseeDetailsTemp;
import com.register.model.RegisterAdditionalDetailsTemp;
import com.register.model.RegisterBasicInfo;

public interface RegistrationService {

    List<MarketMstr> fetchAllMarketsPublic(MarketMstr marketObject);

    List<OrgCategoryMaster> getOrgTypeForSignUp();

    void getEnrollDetails(RegistrationMstr regMaster, String token, ResponseBean responseBean);

    String createOTP(MessageTracker messageTracker);

    ResponseMessage editBasicInfo(RegisterBasicInfo basicInfoObj);

    void saveBasicInfo(BasicInfoDTO dto, String token);

    void savePermanentAddress(String userId, PermanentAddressDTO dto);

    // void saveBusinessAddress(RegisterBusinessAddressDTO dto);

    void savePermanentAndBusinessAddress(PermanentAddressDTO dto, String token);

    void saveAdditionalDetails(AdditionalDetailsDTO dto, String token);

    void approveRejectRegistration(String token, ScrutinyRequestDTO request);

    DocumentUploadTemp saveFile(String token, String docType, MultipartFile file) throws IOException;

    ResponseMessage checkValidFile(MultipartFile file);

    void saveBankGstDetails(BankGstDTO dto, String token);

    LicenseeDetailsTemp saveLicenseeDetails(LicenseeDetailsTemp details, String token);

    boolean checkMandatoryTerms(String formType, int totalMandatoryCount, String token);

    void saveFinalSubmissionTest(FinalRegistrationFormDTO form) throws IOException;

    UserMstr changePassword(ChangePasswordDTO request);

    Map<String, Object> getEnrollDetails(String orgId);

    boolean verifyOTP(String mobileNo, String inputOtp);

    List<TermsAndConditionsResponseDTO> getTermsByFormType(String formType);

    void saveFinalSubmission(String token) throws IOException;

    RegistrationPreviewDTO getRegistrationPreview(String token, String orgId);

    boolean saveUserThumbPrint(String token, UserDigiSignRequestDto dto);

    public Optional<String> getUserThumbPrint(String token);

    UserMstr changePasswordOTP(ChangeDefaultPasswordDTO request);

    boolean validateOTP(OtpValidateDTO request);

}
