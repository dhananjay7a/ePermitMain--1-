package com.organisation.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.register.dto.ChangePasswordDTO;
import com.organisation.constants.OrgConstants;
import com.organisation.dto.ChangeDefaultPasswordDTO;
import com.organisation.dto.OtpValidateDTO;
import com.organisation.model.DropDownMaster;
import com.organisation.model.MarketMaster;
import com.organisation.model.MessageTracker;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.ResponseMessage;
import com.organisation.model.UserMstr;
import com.organisation.service.OrganisationService;
import com.organisation.service.RegistrationService;

@CrossOrigin(origins = "*")
@RestController
public class OrganisationController {

	@Autowired
	private OrganisationService orgService;

	@Autowired
	private RegistrationService regService;

	private static final Logger log = LoggerFactory.getLogger(OrganisationController.class);

	@RequestMapping(value = "/OrgTypeForSignUp", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<List<DropDownMaster>> orgTypeForSignup() {
		log.info("Inside orgTypeForSignup():: OrganisationController");
		List<DropDownMaster> dropDownList = orgService.getOrgTypeForSignUp();
		if (dropDownList == null) {
			return new ResponseEntity<List<DropDownMaster>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<DropDownMaster>>(dropDownList, HttpStatus.OK);
		}
	}

	@PostMapping("/fetchAllMarkets")
	public ResponseEntity<List<MarketMaster>> fetchAllMarkets(@RequestBody MarketMaster marketObject) {
		log.info("Inside fetchAllMarkets():: OrgController");
		List<MarketMaster> marketComMapLst = orgService.fetchAllMarketsPublic(marketObject);
		if (marketComMapLst == null || marketComMapLst.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
		}
		log.info("Exiting from fetchAllMarkets():: OrgController");
		return new ResponseEntity<List<MarketMaster>>(marketComMapLst, HttpStatus.OK);
	}

	@PostMapping("/createOTP")
	public ResponseEntity<String> createOTP(@RequestBody MessageTracker messageTrackerObj) {
		log.info("Inside createOtp() :: PublicController");

		String otp = orgService.createOTP(messageTrackerObj);

		if (otp != null) {
			return ResponseEntity.ok(otp); // Return the OTP in the response body
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP creation failed");
		}

	}

	@PostMapping("/enrollOrganization")
	public ResponseEntity<ResponseMessage> enrollOrganisation(@RequestBody RegistrationMaster registrationObj) {
		log.info("Inside enrollOrganisation():: OrgController");

		ResponseMessage response = orgService.enrollOrganisation(registrationObj, true);

		if (response == null) {
			log.error("Organization enrollment failed: null response from service");
			ResponseMessage errorResponse = new ResponseMessage();
			errorResponse.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
			errorResponse.setMessage("Organization enrollment failed due to an unknown error.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (OrgConstants.ERROR_CODES.NO_ERROR == response.getErrorCode()) {
			response.setMessage("MSG_ORG_SELF_ENROLL_SUCCESS");
		} else {
			response.setMessage(Objects.toString(response.getErrorCode()));
		}
		log.info("Exiting from enrollOrganisation():: OrgController");
		return new ResponseEntity<ResponseMessage>(response, HttpStatus.OK);
	}

	@PostMapping("/changePasswordCreateOTP")
	public ResponseEntity<String> changePasswordOTP(@RequestBody ChangeDefaultPasswordDTO request) {
		log.info("Inside changePasswordCreateOTP() :: RegController ");
		UserMstr user = null;
		try {
			user = regService.changePasswordOTP(request);
			if (user != null) {
				log.info("Exiting from changePasswordCreateOTP() :: RegController");
				return ResponseEntity.ok("OTP send successfully");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
		} catch (Exception e) {
			log.error("Error changing password", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing password");
		}
	}

	@PostMapping("/validateOTP")
	public ResponseEntity<String> validateOTP(@RequestBody OtpValidateDTO request) {
		log.info("Inside validateOTP() :: RegController");
		try {
			boolean isValid = regService.validateOTP(request);
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
			log.info("Exit from validateOTP() :: RegController");
		}
	}

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

	@PostMapping("/changeForgotUsrPwd")
	public ResponseEntity<RegistrationMaster> changeForgotUserpassword(
			@RequestBody RegistrationMaster registrationObj) {
		log.info("Entring changeForgotUserpassword method");

		registrationObj = orgService.changeForgotUserpassword(registrationObj);

		return new ResponseEntity<RegistrationMaster>(registrationObj, HttpStatus.OK);

	}

	@PostMapping("/forgotPasswordResendOtp")
	public ResponseEntity<RegistrationMaster> forgotPasswordResendOtp(@RequestBody RegistrationMaster registrationObj) {
		log.info("Entering forgotPasswordResendOtp");

		registrationObj = orgService.forgotPasswordResendOtp(registrationObj);

		return new ResponseEntity<RegistrationMaster>(registrationObj, HttpStatus.OK);
	}

	@PostMapping("/checkValidUserOTP")
	public ResponseEntity<RegistrationMaster> checkValidUserOTP(@RequestBody RegistrationMaster registrationObj) {

		log.info("In checkValidUserOTP() Password User " + registrationObj.getPassword());

		registrationObj = orgService.checkValidUserOTP(registrationObj);

		return new ResponseEntity<RegistrationMaster>(registrationObj, HttpStatus.OK);

	}

}
