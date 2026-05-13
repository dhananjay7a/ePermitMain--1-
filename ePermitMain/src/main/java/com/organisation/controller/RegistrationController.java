package com.organisation.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epermit.Exception.FeeAlreadyPaidException;
import com.epermit.Exception.FeeNotPaidException;
import com.epermit.register.dto.AdditionalDetailsDTO;
import com.epermit.register.dto.BankGstDTO;
import com.epermit.register.dto.BasicInfoDTO;
import com.epermit.register.dto.ChangePasswordDTO;
import com.epermit.register.dto.FinalRegistrationFormDTO;
import com.epermit.register.dto.OrgIdDTO;
import com.epermit.register.dto.OtpVerificationRequest;
import com.epermit.register.dto.PermanentAddressDTO;
import com.epermit.register.dto.PreviewRequestDTO;
import com.epermit.register.dto.RegDetailsResponseDTO;
import com.epermit.register.dto.RegistrationPreviewDTO;
import com.epermit.register.dto.ScrutinyRequestDTO;
import com.epermit.register.dto.TermsAndConditionsDTO;
import com.epermit.register.dto.TermsAndConditionsResponseDTO;
import com.epermit.register.dto.UserDigiSignRequestDto;
import com.organisation.constants.OrgConstants;
import com.organisation.dto.ApprovalResponseDTO;
import com.organisation.dto.ApproveRejectRegistrationDTO;
import com.organisation.dto.ChangeDefaultPasswordDTO;
import com.organisation.dto.ScrutinyListRequestDto;
import com.organisation.model.MarketMstr;
import com.organisation.model.MessageTracker;
import com.organisation.model.OrgCategoryMaster;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.ResponseMessage;
import com.organisation.model.UserMstr;
import com.organisation.repository.DocumentUploadTempRepository;
import com.organisation.responsehandler.ApiResponses;
import com.organisation.responsehandler.ResponseBean;
import com.organisation.security.TokenService;
import com.organisation.service.ApprovalWorkflowService;
import com.organisation.service.RegistrationService;
import com.organisation.util.OrgUtil;
import com.register.model.BankGstDetails;
import com.register.model.DocumentUploadTemp;
import com.register.model.EncryptedRequest;
import com.register.model.LicenseeDetailsTemp;
import com.register.model.RegisterAdditionalDetailsTemp;
import com.register.model.RegisterBasicInfo;

import io.jsonwebtoken.Claims;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping(value = "/register")
public class RegistrationController {

	@Autowired
	private RegistrationService regService;

	@Autowired
	private TokenService ts;

	@Autowired
	private DocumentUploadTempRepository documentUploadRepo;

	@Autowired
	private ApprovalWorkflowService approvalWorkflowService;
	
	@Autowired
	private ResponseBean responseBean;


	private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

	// Final (from new table) - getting enroll details in the time complete
	// registration
	@PostMapping("/getEnrollDetails")
	public ResponseEntity<Map<String, Object>> getEnrollDetails(@RequestBody OrgIdDTO request) {
		log.info("Inside getEnrollDetails() :: RegController for orgId = {}", request.getOrgId());
		Map<String, Object> response = new HashMap<>();
		try {
			response = regService.getEnrollDetails(request.getOrgId());
		} catch (Exception e) {
			log.error("Error in getRegDetails1 for orgId = {}: {}", request.getOrgId(), e.getMessage(), e);
		}
		log.info("Exiting from getEnrollDetails() :: RegController");
		return ResponseEntity.ok(response);
	}

	// final
	@PostMapping("/basicInfo")
	public ApiResponses saveBasicInfo(@RequestHeader(name = "Authorization") String token,
			@RequestBody BasicInfoDTO dto) {

		log.info("Inside saveBasicInfo() :: RegController");
		ResponseBean responseBean = new ResponseBean();

		try {
			regService.saveBasicInfo(dto, token);
			responseBean.AllResponse("Success", null); // 👈 Same standardized response
		} catch (RuntimeException ex) {
			log.error("Token/Save Error: {}", ex.getMessage());
			if (ex.getMessage().contains("Invalid Token")) {
				responseBean.AllResponse("TokenInvalid", null);
			} else {
				responseBean.AllResponse("Fail", null);
			}
		} catch (Exception e) {
			responseBean.AllResponse("Error", null);
		}
		return responseBean.getResponse();
	}

	// final
	@PostMapping("/existingLicenseDetails")
	public ApiResponses saveLicenseeDetails(@RequestHeader(name = "Authorization") String token,
			@RequestBody LicenseeDetailsTemp details) {

		log.info("Inside saveLicenseeDetails() :: LicenseeDetailsController");
		ResponseBean responseBean = new ResponseBean();

		try {
			regService.saveLicenseeDetails(details, token);
			responseBean.AllResponse("Success", null);

		} catch (RuntimeException ex) {
			log.error("Token/Save Error: {}", ex.getMessage());
			if (ex.getMessage().contains("Invalid Token")) {
				responseBean.AllResponse("TokenInvalid", null);
			} else {
				responseBean.AllResponse("Fail", null);
			}
		} catch (Exception e) {
			responseBean.AllResponse("Error", null);
		}

		return responseBean.getResponse();
	}

	// final
	@PostMapping("/permanentAndBusinessAddress")
	public ApiResponses savePermanentAndBusinessAddress(@RequestHeader(name = "Authorization") String token,
			@RequestBody PermanentAddressDTO dto) {

		log.info("Inside savePermanentAndBusinessAddress() :: RegController");
		ResponseBean response = new ResponseBean();

		try {
			regService.savePermanentAndBusinessAddress(dto, token); // 👈 pass token to service
			response.AllResponse("Success", null);
		} catch (RuntimeException ex) {
			if (ex.getMessage().contains("Invalid Token")) {
				response.AllResponse("TokenInvalid", null);
			} else {
				response.AllResponse("Fail", null);
			}
		} catch (Exception ex) {
			response.AllResponse("Error", null);
		}

		return response.getResponse();
	}

	// Final
	@PostMapping("/bankGst")
	public ApiResponses saveBankDetails(@RequestHeader(name = "Authorization") String token,
			@RequestBody BankGstDTO dto) {
		log.info("Inside saveBankDetails():: RegController");
		ResponseBean response = new ResponseBean();

		try {
			regService.saveBankGstDetails(dto, token); // 👈 pass token to service
			response.AllResponse("Success", null);
		} catch (RuntimeException ex) {
			if (ex.getMessage().contains("Invalid Token")) {
				response.AllResponse("TokenInvalid", null);
			} else {
				response.AllResponse("Fail", null);
			}
		} catch (Exception ex) {
			response.AllResponse("Error", null);
		}

		return response.getResponse();
	}

	// final
	@PostMapping("/additionalDetails")
	public ApiResponses saveAdditionalDetails(@RequestHeader(name = "Authorization") String token,
			@RequestBody AdditionalDetailsDTO dto) {

		log.info("Inside saveAdditionalDetails() :: RegController");
		ResponseBean response = new ResponseBean();

		try {
			regService.saveAdditionalDetails(dto, token); // 👈 Call service with token
			response.AllResponse("Success", null);

		} catch (RuntimeException ex) {
			if (ex.getMessage().contains("Invalid Token")) {
				response.AllResponse("TokenMissing", null);
			} else {
				response.AllResponse("Fail", null);
			}
		} catch (Exception ex) {
			response.AllResponse("Error", null);
		}

		return response.getResponse();
	}

	// Final
	@PostMapping("/uploadDocuments")
	public ResponseEntity<?> uploadDocuments(@RequestHeader(name = "Authorization") String token,
			@RequestParam("docType") List<String> docTypes, @RequestParam("files") List<MultipartFile> files) {

		log.info("Inside uploadDocuments()");

		List<Map<String, String>> uploadResults = new ArrayList<>();

		try {
			for (int i = 0; i < files.size(); i++) {
				MultipartFile file = files.get(i);
				String docType = docTypes.get(i);

				// Validate the file extension and magic bytes
				ResponseMessage result = regService.checkValidFile(file);
				if (result.getErrorCode() != OrgConstants.ERROR_CODES.NO_ERROR) {
					Map<String, String> errorMap = new HashMap<>();
					errorMap.put("docType", docType);
					errorMap.put("error", result.getMessage());
					uploadResults.add(errorMap);
					continue;
				}

				// 👉 Pass ONLY token, NO orgId
				DocumentUploadTemp saved = regService.saveFile(token, docType, file);

				Map<String, String> resultMap = new HashMap<>();
				resultMap.put("docType", docType);
				resultMap.put("docName", saved.getDocName());
				resultMap.put("status", "Uploaded");
				uploadResults.add(resultMap);
			}

			return ResponseEntity.ok(uploadResults);

		} catch (RuntimeException ex) {
			if (ex.getMessage().contains("Invalid Token")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
			}

		} catch (Exception e) {
			log.error("Error uploading files: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading files: " + e.getMessage());
		}
	}

	@PostMapping("/checkTermsAndConditions")
	public ApiResponses checkTermsAndConditions(@RequestHeader(name = "Authorization") String token,
			@RequestBody TermsAndConditionsDTO dto) {

		log.info("Inside checkTermsAndConditions() :: RegController");
		ResponseBean response = new ResponseBean();

		try {
			boolean result = regService.checkMandatoryTerms(dto.getFormType(), dto.getTotalMandatoryCount(), token);

			if (result) {
				response.AllResponse("Success", result);
			} else {
				response.AllResponse("Fail", result);
				response.getResponse().setData(result); // 📌 ensure false is returned
			}

		} catch (Exception ex) {
			response.AllResponse("Error", null);
		}

		return response.getResponse();
	}

	// final
	@PostMapping("/finalSubmit")
	public ApiResponses finalSubmission(
			@RequestHeader(name = "Authorization") String token) {

		log.info("Inside finalSubmit() :: RegController");
		ResponseBean response = new ResponseBean();

		try {
			regService.saveFinalSubmission(token);
			response.AllResponse("Success", "Final form submitted successfully.");

		} catch (FeeNotPaidException e) {
			// ✅ THIS IS YOUR CASE
			response.AllResponse("FeeNotPaid", null);

		} catch (FeeAlreadyPaidException e) {
			response.AllResponse("Exists", null);

		} catch (RuntimeException ex) {
			if (ex.getMessage() != null && ex.getMessage().contains("Invalid Token")) {
				response.AllResponse("TokenInvalid", null);
			} else {
				response.AllResponse("Error", null);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			response.AllResponse("Error", null);
		}

		return response.getResponse();
	}

	// Testing purpose with all json data
	@PostMapping("/finalSubmitTest")
	public ResponseEntity<String> finalSave(@RequestBody EncryptedRequest request) {
		log.info("Inside finalSave():: RegController");
		try {
			FinalRegistrationFormDTO formDTO = OrgUtil.decrypt(request, FinalRegistrationFormDTO.class);
			regService.saveFinalSubmissionTest(formDTO);
			return ResponseEntity.ok("Final form submitted successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Submission failed: " + e.getMessage());
		}
	}

	// final
	@PostMapping("/fetchAllMarkets")
	public ResponseEntity<List<MarketMstr>> fetchAllMarkets(@RequestBody MarketMstr marketMstr) {
		log.info("Inside fetchAllMarkets() :: RegController");

		List<MarketMstr> marketList = regService.fetchAllMarketsPublic(marketMstr);
		if (marketList == null || marketList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		log.info("Exiting fetchAllMarkets() :: RegController");
		return new ResponseEntity<>(marketList, HttpStatus.OK);
	}

	@RequestMapping(value = "/OrgTypeForSignUp", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<List<OrgCategoryMaster>> orgTypeForSignup() {
		log.info("Inside orgTypeForSignup() :: RegController");

		try {
			List<OrgCategoryMaster> categoryList = regService.getOrgTypeForSignUp();

			if (categoryList == null || categoryList.isEmpty()) {
				log.warn("No organization categories found for sign up.");
				return ResponseEntity.noContent().build();
			}

			return ResponseEntity.ok(categoryList);

		} catch (Exception e) {
			log.error("Exception in orgTypeForSignup(): ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Final
	@PostMapping("/createOTP")
	public ResponseEntity<String> createOTP(@RequestBody MessageTracker messageTrackerObj) {
		log.info("Inside createOtp() :: PublicController");

		String otp = regService.createOTP(messageTrackerObj);
		try {
			if (otp != null) {
				return ResponseEntity
						.ok("OTP has been sent to your registered mobile number : " + messageTrackerObj.getMobileNo());
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP creation failed");
			}
		} catch (Exception ex) {
			log.error("Error in createOTP(): {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while creating OTP. Please try again.");
		} finally {
			log.info("Exit from createOTP() :: PublicController");
		}
	}

	// Final - otp verification
	@PostMapping("/verifyOTP")
	public ResponseEntity<String> verifyOTP(@RequestBody OtpVerificationRequest request) {
		log.info("Inside verifyOTP() :: RegController");
		try {
			boolean isValid = regService.verifyOTP(request.getMobileNo(), request.getOtp());
			if (isValid) {
				return ResponseEntity.ok("OTP verified successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or Expired OTP, please try again.");
			}
		} catch (Exception ex) {
			log.error("Error in verifyOTP(): {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while verifying OTP. Please try again.");
		} finally {
			log.info("Exit from verifyOTP() :: RegController");
		}
	}

	// Final - for change default password after complete the Enrollment
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO request) {
		log.info("Inside changePassword() :: RegController ");
		UserMstr user = null;
		try {
			user = regService.changePassword(request);
			if (user != null) {
				log.info("Exiting from changePassword() :: RegController");
				return ResponseEntity.ok("Password changed successfully");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
		} catch (Exception e) {
			log.error("Error changing password", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing password");
		}
	}

	// final
	@GetMapping("/getTerms/{formType}")
	public ResponseEntity<?> getTermsByFormType(@PathVariable String formType) {
		log.info("Request received to fetch Terms & Conditions for formType: {}", formType);

		try {
			List<TermsAndConditionsResponseDTO> response = regService.getTermsByFormType(formType);

			if (response == null || response.isEmpty()) {
				log.warn("No Terms & Conditions found for formType: {}", formType);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No terms found for formType: " + formType);
			}

			log.info("Successfully fet ched {} terms and conditions for formType: {}", response.size(), formType);
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			log.error("Error fetching Terms & Conditions for formType: {}", formType, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching terms for formType: " + formType);
		}
	}

	// final (from old table) : for getting the details
	@PostMapping("/getRegDetails")
	public ApiResponses getRegDetails(
			@RequestBody RegistrationMstr regMasterObj,
			@RequestHeader(value = "Authorization") String token) {

		log.info("Inside getRegDetails() :: RegistrationController - OrgId: {}",
				regMasterObj.getOrgId());

		ResponseBean responseBean = new ResponseBean();

		try {
			regService.getEnrollDetails(regMasterObj, token, responseBean);
		} catch (Exception e) {
			log.error("Controller Exception : {}", e.getMessage());
			responseBean.AllResponse("Error", null);
		}

		log.info("Exit from getRegDetails() :: RegistrationController");
		return responseBean.getResponse();
	}

	// Not used currently
	@PostMapping("/basicInfo2")
	public ResponseEntity<Map<String, Object>> editBasicInfo(@RequestBody EncryptedRequest request) {
		Map<String, Object> responseMap = new HashMap<>();

		RegisterBasicInfo regBasicInfo = OrgUtil.decrypt(request, RegisterBasicInfo.class);
		if (regBasicInfo == null) {
			responseMap.put("message", "Invalid encrypted data.");
			return ResponseEntity.badRequest().body(responseMap);
		}
		log.info("Inside editBasicInfo():: RegistrationController for userId: " + regBasicInfo.getUserId());

		try {

			ResponseMessage serviceResponse = regService.editBasicInfo(regBasicInfo);

			if (OrgConstants.ERROR_CODES.NO_ERROR == serviceResponse.getErrorCode()) {
				responseMap.put("message", "MSG_ORG_EDIT_SUCCESS");
			} else {
				responseMap.put("message", Objects.toString(serviceResponse.getErrorCode()));
			}
			responseMap.put("errorCode", serviceResponse.getErrorCode()); // optional

		} catch (Exception e) {
			log.error("Registration edit failed", e);
			responseMap.put("message", "Registration edit failed due to an internal error.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
		}

		log.info("Exiting from editBasicInfo() :: RegistrationController");
		return ResponseEntity.ok(responseMap);
	}

	// for testing only : not used
	// cooment by Rahul
	// @PostMapping("/basic-info1")
	// public String saveBasicInfo(@RequestBody BasicInfoDTO dto) {
	// log.info("Inside saveBasicInfo() :: RegController");
	// try {
	// regService.saveBasicInfo(dto);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// log.info("Exiting from saveBasicInfo() :: RegController");
	// return "Basic Info Saved Successfully";
	// }

	// // testing : and not used
	// @PostMapping("/additional-details1")
	// public ResponseEntity<?> saveAdditionalDetails1(@RequestBody
	// RegisterAdditionalDetailsTemp additionalDetails) {
	// try {
	// log.info("Inside saveAdditionalDetails() :: RegController - userId: " +
	// additionalDetails.getOrgId());
	//
	// RegisterAdditionalDetailsTemp savedDetails =
	// regService.saveAdditionalDetails(additionalDetails);
	//
	// log.info("Exiting from saveAdditionalDetails() :: RegController");
	// return ResponseEntity.status(201).body(savedDetails); // 201 Created
	// } catch (Exception e) {
	// log.error("Exception in saveAdditionalDetails() :: RegController - Error:
	// {}", e.getMessage(), e);
	//
	// // You can return a generic error or a structured error response
	// return ResponseEntity.status(500)
	// .body("Internal Server Error: Unable to save additional details. Please try
	// again later.");
	// }
	// }

	// Testing : not used now
	@PostMapping("/upload-documents1")
	public ResponseEntity<?> uploadDocuments1(@RequestParam("orgId") String orgId,
			@RequestParam("docType") List<String> docTypes, @RequestParam("files") List<MultipartFile> files) {

		log.info("Inside uploadDocuments() :: RegController - orgId : " + orgId);
		List<Map<String, String>> uploadResults = new ArrayList<>();
		try {
			for (int i = 0; i < files.size(); i++) {
				MultipartFile file = files.get(i);
				String docType = docTypes.get(i);

				DocumentUploadTemp saved = regService.saveFile(orgId, docType, file);

				Map<String, String> result = new HashMap<>();
				result.put("docType", docType);
				result.put("docName", saved.getDocName());
				result.put("status", "Uploaded");
				uploadResults.add(result);
			}
			return ResponseEntity.ok(uploadResults);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error uploading files: " + e.getMessage());
		}

	}

	// testing
	// @PostMapping("/permanent-and-business-address1")
	// public ResponseEntity<String> savePermanentAndBusinessAddress1(@RequestBody
	// PermanentAddressDTO dto) {
	// log.info("Inside savePermanentAndBusinessAddress() :: RegController - orgId :
	// " + dto.getOrgId());
	//
	// try {
	// regService.savePermanentAndBusinessAddress(dto);
	// log.info("Exiting from savePermanentAndBusinessAddress() :: RegController -
	// UserId: " + dto.getOrgId());
	// return ResponseEntity.ok("Permanent and Business addresses saved
	// successfully");
	// } catch (Exception e) {
	// log.error("Error in savePermanentAndBusinessAddress(): ", e);
	// return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
	// }
	// }

	// For testing only
	// @PostMapping("/checkTermsAndConditions1")
	// public ResponseEntity<Boolean> checkTermsAndConditions1(@RequestBody
	// TermsAndConditionsDTO request) {
	//
	// log.info("Inside checkTermsAndConditions() :: RegController");
	// try {
	// boolean result = regService.checkMandatoryTerms(request.getFormType(),
	// request.getTotalMandatoryCount());
	// return ResponseEntity.ok(result);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return ResponseEntity.status(500).body(false);
	// }
	// }

	// Testing from the postman - before the encryption
	@PostMapping("/final-submit1")
	public ResponseEntity<String> finalSave1(@RequestBody FinalRegistrationFormDTO formDTO) {
		log.info("Inside finalSave():: RegController");
		try {
			regService.saveFinalSubmissionTest(formDTO);
			return ResponseEntity.ok("Final form submitted successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Submission failed: " + e.getMessage());
		}
	}

	@PostMapping("/finalSubmit1234")
	public ResponseEntity<String> finalSave1234(@RequestBody OrgIdDTO formDTO) {
		log.info("Inside finalSave():: RegController");
		try {
			regService.saveFinalSubmission(formDTO.getOrgId());
			return ResponseEntity.ok("Final form submitted successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Submission failed: " + e.getMessage());
		}
	}

	// for preview

	@PostMapping("/preview")
	public ResponseEntity<ApiResponses> previewRegistration(@RequestHeader("Authorization") String token,
			@RequestBody PreviewRequestDTO request) {

		log.info("POST /preview for orgId={}", request.getOrgId());

		ResponseBean responseBean = new ResponseBean();

		try {

			RegistrationPreviewDTO preview = regService.getRegistrationPreview(token, request.getOrgId());

			responseBean.AllResponse("Success", preview);
			return ResponseEntity.ok(responseBean.getResponse());

		} catch (Exception ex) {

			log.error("Preview API error", ex);
			responseBean.AllResponse("Error", null);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
		}
	}

	// Scrutiny/final approve/reject by rahul pal
	/*
	 * @PostMapping("/approveRejectRegistration")
	 * public ApiResponses approveRejectRegistration(@RequestHeader("Authorization")
	 * String token,
	 * 
	 * @RequestBody ScrutinyRequestDTO request) {
	 * log.info("Inside preview() :: RegController");
	 * ResponseBean responseBean = new ResponseBean();
	 * 
	 * try {
	 * 
	 * regService.approveRejectRegistration(token, request);
	 * 
	 * responseBean.AllResponse("Success", "Success");
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * 
	 * responseBean.AllResponse("Error", "Something went wrong");
	 * }
	 * 
	 * return responseBean.getResponse();
	 * }
	 */

	// Scrutiny/final approve/reject by dhananjay pandit
	@PostMapping("/approveRejectRegistration")
	public ResponseEntity<ApiResponses> approveRejectRegistration(@RequestHeader("Authorization") String token,
			@RequestBody ApproveRejectRegistrationDTO request) {

		ResponseBean responseBean = new ResponseBean();

		try {
			log.info("Received  approval request for OrgId: {}", request.getOrgId());

			// Process approval

			ApprovalResponseDTO response = approvalWorkflowService
					.approveRejectRegistration(token, request);
			if (response == null || response.getErrorCode() < 0) {
				log.error("Approval processing failed for OrgId: {}", request.getOrgId());
				responseBean.AllResponse("Error", response.getMessage());
				return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			responseBean.AllResponse("Success", response);

			log.info("Approval request processed successfully for OrgId: {}", request.getOrgId());

			return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error processing plain text approval request", e);
			responseBean.AllResponse("Error", e.getMessage());
			return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// approve reject with pdf file upload by dhananjay pandit
	@PostMapping("/approveRejectRegistration/with-file")
	public ResponseEntity<ApiResponses> approveRejectRegistration(
			@RequestHeader("Authorization") String token,
			@RequestPart("request") ApproveRejectRegistrationDTO request,
			@RequestPart("file") MultipartFile pdfFile) {

		ResponseBean responseBean = new ResponseBean();

		if (pdfFile == null || pdfFile.isEmpty()) {
			log.error("No file uploaded for approval request of OrgId: {}", request.getOrgId());
			responseBean.AllResponse("Error", "PDF file is required for approval.");
			return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.BAD_REQUEST);
		}

		try {

			// Process approval

			ApprovalResponseDTO response = approvalWorkflowService
					.approveRejectRegistrationWithFile(token, request, pdfFile);
			if (response == null || response.getErrorCode() < 0) {
				log.error("Approval processing failed for OrgId: {}", request.getOrgId());
				responseBean.AllResponse("Error", response.getMessage());
				return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			responseBean.AllResponse("Success", response);

			log.info("Approval request processed successfully for OrgId: {}", request.getOrgId());

			return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error processing plain text approval request", e);
			responseBean.AllResponse("Error", e.getMessage());
			return new ResponseEntity<>(responseBean.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/get-signed-pdf/{orgId}")
	public ResponseEntity<byte[]> getSignedPdf(@RequestHeader("Authorization") String token,
			@PathVariable String orgId) {
		try {
			// Fetch PDF bytes from service
			byte[] fileBytes = approvalWorkflowService.getSignedPdf(token, orgId);

			// Validate result
			if (fileBytes == null || fileBytes.length == 0) {
				log.warn("Signed PDF not found or empty for OrgId: {}", orgId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			// Return PDF with proper headers
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"inline; filename=" + orgId + "_Form4.pdf")
					.contentLength(fileBytes.length)
					.body(fileBytes);

		} catch (IllegalArgumentException e) {
			// Validation / bad input errors
			log.error("Invalid request for OrgId {}: {}", orgId, e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		} catch (RuntimeException e) {
			// Business / service-level errors
			log.error("Business error while fetching signed PDF for OrgId {}", orgId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		} catch (Exception e) {
			// Absolute fallback (should rarely happen)
			log.error("Unexpected error while fetching signed PDF for OrgId {}", orgId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GetMapping("/get-unsigned-pdf/{orgId}")
	public ResponseEntity<byte[]> getUnsignedPdf(
			@RequestHeader("Authorization") String token,
			@PathVariable String orgId) {

		try {
			// Fetch PDF bytes from service
			byte[] fileBytes = approvalWorkflowService.getUnsignedPdf(token, orgId);

			// Validate result
			if (fileBytes == null || fileBytes.length == 0) {
				log.warn("Unsigned PDF not found or empty for OrgId: {}", orgId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			// Return PDF with proper headers
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"inline; filename=" + orgId + "_Form4.pdf")
					.contentLength(fileBytes.length)
					.body(fileBytes);

		} catch (IllegalArgumentException e) {
			// Validation / bad input errors
			log.error("Invalid request for OrgId {}: {}", orgId, e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		} catch (RuntimeException e) {
			// Business / service-level errors
			log.error("Business error while fetching unsigned PDF for OrgId {}", orgId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		} catch (Exception e) {
			// Absolute fallback (should rarely happen)
			log.error("Unexpected error while fetching unsigned PDF for OrgId {}", orgId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/api/docs/view/{id}")
	public ResponseEntity<Resource> viewDocument(
			@PathVariable Long id,
			@RequestHeader("Authorization") String authHeader) {

		// 1️⃣ Extract token
		String token = authHeader != null && authHeader.startsWith("Bearer ")
				? authHeader.substring(7)
				: null;

		if (token == null) {
			throw new RuntimeException("Token Missing");
		}

		// 2️⃣ Validate token
		Claims claims = ts.validateToken(token);
		if (claims == null) {
			throw new RuntimeException("Invalid Token");
		}

		// 3️⃣ Extract userId
		String userId = claims.getSubject();

		// 4️⃣ Convert → orgId (remove leading U)
		String orgIdFromToken = userId.startsWith("U")
				? userId.substring(1)
				: userId;

		// 5️⃣ Fetch document record
		DocumentUploadTemp doc = documentUploadRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Document Not Found"));
		//
		// 6️⃣ Check if document belongs to same org
		if (!doc.getOrgId().equals(orgIdFromToken)) {
			throw new RuntimeException("Unauthorized Access");
		}

		// 7️⃣ Load physical file
		Path filePath = Paths.get(doc.getUploadPath());

		if (!Files.exists(filePath)) {
			throw new RuntimeException("File Not Found on Server");
		}

		Resource file;
		try {
			file = new FileSystemResource(filePath.toFile());

		} catch (Exception e) {
			throw new RuntimeException("Unable to read file");
		}

		// 8️⃣ Detect content type
		String contentType = "application/octet-stream";
		try {
			contentType = Files.probeContentType(filePath);
		} catch (Exception ignored) {
		}

		// 9️⃣ Return document
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"" + doc.getDocName() + "\"")
				.body(file);
	}

	// dsign

	@PostMapping("/save-thumbprint")
	public ResponseEntity<ApiResponses> saveThumbPrint(
			@RequestHeader("Authorization") String token,
			@RequestBody UserDigiSignRequestDto dto) {

		// log.info("POST /save-thumbprint for userId={}", dto.getUserId());

		ResponseBean responseBean = new ResponseBean();

		try {

			boolean saved = regService.saveUserThumbPrint(token, dto);

			if (saved) {
				responseBean.AllResponse("Success", dto);
			} else {
				responseBean.AllResponse("Exists", dto);
			}

			return ResponseEntity.ok(responseBean.getResponse());

		} catch (Exception ex) {

			log.error("Save thumbprint API error", ex);
			responseBean.AllResponse("Error", null);
			return ResponseEntity.internalServerError()
					.body(responseBean.getResponse());
		}
	}

	@PostMapping("/get-user-thumbprint")
	public ResponseEntity<ApiResponses> getUserThumbPrint(
			@RequestHeader("Authorization") String token) {

		ResponseBean responseBean = new ResponseBean();

		try {

			Optional<String> thumbPrint = regService.getUserThumbPrint(token);

			if (thumbPrint.isPresent()) {
				responseBean.AllResponse("Success", thumbPrint.get());
			} else {
				responseBean.AllResponse("Notfound", null);
			}

			return ResponseEntity.ok(responseBean.getResponse());

		} catch (Exception ex) {

			log.error("Get thumbprint API error", ex);
			responseBean.AllResponse("Error", null);
			return ResponseEntity.internalServerError()
					.body(responseBean.getResponse());
		}
	}
	
	
	
	//Scrutiny list
	
	

    @PostMapping("/ScrutinyList")
    public Object getScrutinyList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ScrutinyListRequestDto requestDto) {
    	//ResponseBean responseBean = new ResponseBean();

        try {

            // 🔎 Basic null validation
            if (requestDto.getApproverType() == null) {

                responseBean.AllResponse("Nulltype", null);
                return responseBean.getResponse();
            }

            regService.getScrutinyList(token, requestDto);

            return responseBean.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
            responseBean.AllResponse("Error", e.getMessage());
            return responseBean.getResponse();
        }
    }
    
    
    @PostMapping("/FinalApproverList")
    public Object getFinalApproverList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ScrutinyListRequestDto requestDto) {
    	//ResponseBean responseBean = new ResponseBean();

        try {

            // 🔎 Basic null validation
            if (requestDto.getApproverType() == null) {

                responseBean.AllResponse("Nulltype", null);
                return responseBean.getResponse();
            }

            regService.getFinalApproverList(token, requestDto);

            return responseBean.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
            responseBean.AllResponse("Error", e.getMessage());
            return responseBean.getResponse();
        }
    }
    
    
    
    //default password change api
    
    
    @PostMapping("/changeDefaultPassword")
    public ApiResponses changePassword(@RequestBody ChangeDefaultPasswordDTO request) {

        ResponseBean responseBean = new ResponseBean();

 
        try {   
        	
            regService.changeDefaultPassword(request, responseBean);
        } catch (Exception e) {
            log.error("Error changing password", e);
            responseBean.AllResponse("Error", null);
        }

        return responseBean.getResponse();
    }

//Renewal code by Rehan
    
    
    @PostMapping("/editRegForRenewal")
	public ResponseEntity<String> editRegistrationForRenewal(@RequestBody FinalRegistrationFormDTO formDTO) {
		log.info("Inside editRegistrationForRenewal():: RegController");
		try {
			regService.editRegistrationForRenewal(formDTO);
			return ResponseEntity.ok("Renewal Done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Renewal failed: " + e.getMessage());
		}
	}
    
    
    
}
