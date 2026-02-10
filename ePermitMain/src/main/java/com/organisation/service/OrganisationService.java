package com.organisation.service;

import java.sql.Connection;
import java.util.List;

import com.organisation.model.DropDownMaster;
import com.organisation.model.MarketMaster;
import com.organisation.model.MessageTracker;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.ResponseMessage;

public interface OrganisationService {

	List<DropDownMaster> getOrgTypeForSignUp();
	
	List<MarketMaster> fetchAllMarketsPublic(MarketMaster marketObject);
	
	String createOTP(MessageTracker messageTracker);
	
	ResponseMessage enrollOrganisation(RegistrationMaster regMaster, boolean isSelfRegistration);

	ResponseMessage createOrganisation(RegistrationMaster registrationObj);

	ResponseMessage createOrganisation(RegistrationMaster registrationObj, Connection conn);

	String getIsAutoApprovalflag(String orgCategory);

	boolean checkOrgCatIsHost(RegistrationMaster regMaster);

	RegistrationMaster createOrgIds(RegistrationMaster registrationObj, Connection conn);

	RegistrationMaster changeForgotUserpassword(RegistrationMaster registrationObj);

	RegistrationMaster forgotPasswordResendOtp(RegistrationMaster registrationObj);

	RegistrationMaster checkValidUserOTP(RegistrationMaster registrationObj);

}
