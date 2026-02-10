package com.organisation.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.organisation.model.DropDownMaster;
import com.organisation.model.FormFiveModel;
import com.organisation.model.MarketMaster;
import com.organisation.model.MemberMarketMap;
import com.organisation.model.MessageTracker;
import com.organisation.model.RegAdditionalInfo;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.UserMapping;
import com.organisation.model.UserMaster;

public interface OrganisationDao {

	List<DropDownMaster> getOrgTypeForSignUp();

	List<MarketMaster> fetchAllMarketsPublic(MarketMaster marketObject);

	boolean saveOTP(MessageTracker messageTracker);

	boolean checkOrgCatIsHost(RegistrationMaster regMasterObj, Connection conn);

	String getIsAutoApprovalflag(String orgCategory, Connection conn);

	RegistrationMaster fetchScopeOfOrg(RegistrationMaster registrationMaster, Connection conn) throws Exception;

	int createOrgReg(RegistrationMaster registrationObj, Connection conn) throws Exception;

	int createDefaultuser(RegistrationMaster registrationObj, Connection conn) throws Exception;

	int insertRegAddtnInfo(RegAdditionalInfo regAdditionalInfo, Connection conn) throws Exception;

	int insertUpdateForm5Details(FormFiveModel formFiveModel, Connection conn);

	ArrayList<String> fetchAllMarkets();

	MemberMarketMap saveMemberMarketMap(MemberMarketMap mapBean, final Connection conn);

	UserMapping saveUserMarketMapping(UserMapping userMarketMap, final Connection conn);

	boolean createDefaultTraderRole(RegistrationMaster registrationObj, final Connection conn);

	boolean checkIfAccMstrExists(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	int insertAccountMaster(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	int updateAccountMaster(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	boolean createDefaultUserRole(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	String saveMessageTracker(MessageTracker msg);

	RegistrationMaster changeUserPassword(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	UserMaster fetchUserData(UserMaster userMasterObj) throws Exception;

	int updateUserOtp(RegistrationMaster registrationObj, final Connection conn) throws Exception;

	boolean checkValidOTPForUser(RegistrationMaster registrationObj) throws Exception;

}
