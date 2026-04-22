package com.organisation.service.impl;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.organisation.constants.OrgConstants;
import com.organisation.constants.OrgConstants.RENEWAL_STATUS;
import com.organisation.dao.OrganisationDao;
import com.organisation.model.DropDownMaster;
import com.organisation.model.FormFiveModel;
import com.organisation.model.IdGenerator;
import com.organisation.model.MarketMaster;
import com.organisation.model.MemberMarketMap;
import com.organisation.model.MessageTracker;
import com.organisation.model.RegAdditionalInfo;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.ResponseMessage;
import com.organisation.model.UserMapping;
import com.organisation.model.UserMaster;
import com.organisation.model.UserSessionBean;
import com.organisation.service.CommonService;
import com.organisation.service.OrganisationService;
import com.organisation.service.PasswordService;

import com.organisation.util.OrgUtil;
import com.organisation.util.RepositoryThreadLocal;

@Service
public class OrganisationServiceImpl implements OrganisationService {

	@Autowired
	private OrganisationDao orgDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	DataSource ds;

	private static final Logger log = LoggerFactory.getLogger(OrganisationServiceImpl.class);

	@Override
	public List<DropDownMaster> getOrgTypeForSignUp() {
		log.info("Inside getOrgTypeForSignUp():: OrgServiceImpl ");
		List<DropDownMaster> dropDownList = new ArrayList<DropDownMaster>();
		try {

			dropDownList = orgDao.getOrgTypeForSignUp();
		} catch (Exception e) {
			log.error("Exception : ", e.getMessage(), e);
		}

		log.info("Exiting getOrgTypeForSignUp():: OrgServiceImpl");
		return dropDownList;
	}

	@Override
	public List<MarketMaster> fetchAllMarketsPublic(MarketMaster marketObject) {
		log.info("Inside fetchAllMarketsPublic() :: OrgServiceImpl");
		try {
			return orgDao.fetchAllMarketsPublic(marketObject);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		log.info("Exit fetchAllMarketsPublic() :: OrgServiceImpl");
		return Collections.emptyList();
	}
	
	
//	public List<MarketMstr> fetchAllMarketsPublic(MarketMstr marketObject){
//		
//	}

	@Override
	public String createOTP(MessageTracker messageTracker) {
		log.info("Inside createOTP():: OrgServiceImpl");
		String otp = OrgUtil.getOtp(OrgConstants.OTP_SIZE);
		try {

			String msg_parameters = "NEW_USER" + "`" + otp + "`" + OrgUtil.getOTPExpiryTimestamp();
			// String mobile_no = messageTracker.getMobileNo();

			messageTracker.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTracker.setMsgType(OrgConstants.MESSAGE_TYPE.SMS);
			messageTracker.setMessage(msg_parameters);
			// messageTracker.setMobileNo(mobile_no);
			messageTracker.setStatus("P");
			messageTracker.setRemarks("PENDING");
			messageTracker.setOwner("EPERMIT");

			if (orgDao.saveOTP(messageTracker)) {
				return otp;
			}

		} catch (Exception e) {
			log.error("Error: " + e.getMessage(), e);
		}

		log.info("Exit from createOTP():: OrgServiceImpl");
		return null;
	}

	@Override
	public ResponseMessage enrollOrganisation(RegistrationMaster regMaster, boolean isSelfRegistration) {
		log.info("Inside enrollOrganisation() :: OrgServiceImpl ");
		ResponseMessage response = new ResponseMessage();
		try {
			if (isSelfRegistration) {
				if (this.checkOrgCatIsHost(regMaster)) {
					response.setErrorCode(OrgConstants.ERROR_CODES.HOST_SIGN_UP_NOT_ALLOWED);
					return response;
				}
				regMaster.setOrgRequestStatus(OrgConstants.REGISTRATION_STATUS.ENROLLED);
				regMaster.setOrgCreatedBy(OrgConstants.SELF_REGISTRATION);

			} else {
				UserSessionBean user = (UserSessionBean) RepositoryThreadLocal.getValue(OrgConstants.USER_ID);
				regMaster.setOrgRequestStatus(OrgConstants.REGISTRATION_STATUS.PENDING);
				regMaster.setOrgCreatedBy(user.getUserId());
			}

			response = this.createOrganisation(regMaster);

		} catch (Exception e) {
			response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
			log.error(OrgConstants.ERROR_CODES.REGISTRATION_FAILED + "Error in enrollOrganization . Exception : "
					+ e.getMessage() + e.getStackTrace());
			e.printStackTrace();
		}

		log.info("Exiting enrollOrganization() :: OrgServiceImpl");
		return response;
	}

	@Override
	public ResponseMessage createOrganisation(RegistrationMaster registrationObj) {
		log.info("Inside createOrganisation() :: OrgServiceImpl ");

		Connection conn = null;
		ResponseMessage response = new ResponseMessage();
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			registrationObj.setIsAutoApprove(this.getIsAutoApprovalflag(registrationObj.getOrgCategory()));
			response = this.createOrganisation(registrationObj, conn);

		} catch (Exception e) {
			response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
			e.printStackTrace();
			log.error(OrgConstants.ERROR_CODES.REGISTRATION_FAILED + " " + e.getMessage() + e.getStackTrace());
		} finally {
			try {
				if (conn != null) {
					conn.commit();
					log.info("Transaction committed successfully.");

					try {
						this.sendOTPToOrg(registrationObj); // Call OTP method safely
						log.info("OTP sent successfully.");
					} catch (Exception otpEx) {
						log.error("Error sending OTP: " + otpEx.getMessage(), otpEx);
					}

				} else {
					log.warn("Transaction rolled back due to error code.");
				}
			} catch (Exception e) {
				log.error("Exception during commit/rollback: " + e.getMessage(), e);
			} finally {
				
			}
		}

		// finally {
		//
		// try {
		// if (response.getErrorCode() == OrgConstants.ERROR_CODES.NO_ERROR) {
		// conn.commit();
		//
		// this.sendOTPToOrg(registrationObj); // do later
		//
		// } else {
		//
		// conn.rollback();
		//
		// }
		// DBUtil.closeConnection(conn);
		//
		// } catch (Exception e) {
		// log.error("Exception : " + e.getMessage());
		// }
		//
		// }
		log.info("Exiting from createOrganisation() :: OrgServiceImpl ");
		return response;

	}

	public void sendOTPToOrg(RegistrationMaster registrationObj) throws Exception {
		log.info("Inside sendotpToOrg():: OrgServiceImpl");
		try {

			if (registrationObj.getOrgContactMobile() != null) {
				registrationObj.setOrgContactMobile(registrationObj.getOrgContactMobile().trim());
			}
			String expiryTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a")
					.format(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
			String messageParameters = registrationObj.getUserId() + "`" + registrationObj.getPassword() + "`"
					+ expiryTime;

			MessageTracker messageTrackerObj = new MessageTracker();
			messageTrackerObj.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTrackerObj.setMsgType(OrgConstants.MESSAGE_TYPE.SMS);
			messageTrackerObj.setMessage(messageParameters);
			messageTrackerObj.setMobileNo(registrationObj.getOrgContactMobile());
			messageTrackerObj.setStatus(OrgConstants.DEFAULT_SMS_STATUS);
			messageTrackerObj.setOwner(OrgConstants.OWNER_STRING);

			String response = orgDao.saveMessageTracker(messageTrackerObj);

			log.info("Exiting sendOTPToOrg() sOrgSrviceImpl " + registrationObj.getOrgId() + "::" + response);

		} catch (Exception e) {
			log.error("Error in saveOTPDataToDB(): " + e.getMessage(), e);
			throw e;
		}

	}

	@Override
	public ResponseMessage createOrganisation(RegistrationMaster registrationObj, final Connection conn) {
		log.info("Inside createOrganisation() serviceImpl" + registrationObj.getOrgId() + " User Id:"
				+ registrationObj.getUserId());

		RegistrationMaster regMstrObj = null;
		ResponseMessage response = new ResponseMessage();
		try {
			regMstrObj = new RegistrationMaster();

			registrationObj = this.createOrgIds(registrationObj, conn);

			if (!OrgConstants.SUCCESS_STRING.equals(registrationObj.getReturnMsg())) {
				response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
				return response;
			}

//			if (OrgUtil.isNeitherNullNorEmpty(registrationObj.getRegAddtnInfo())) {
//				registrationObj.setRegAddtnInfo(new RegAdditionalInfo());
//			}
//			
			if (registrationObj.getRegAddtnInfo() == null) {
				// Initialize if null to prevent NullPointerException
				RegAdditionalInfo regAdditionalInfo = new RegAdditionalInfo();
				regAdditionalInfo.setOrgId(registrationObj.getOrgId());
				regAdditionalInfo.setCreatedBy(registrationObj.getOrgCreatedBy());
				registrationObj.setRegAddtnInfo(regAdditionalInfo);
			} else {
				// Set Org ID if already present
				registrationObj.getRegAddtnInfo().setOrgId(registrationObj.getOrgId());
				registrationObj.getRegAddtnInfo().setCreatedBy(registrationObj.getOrgCreatedBy());
			}

//			registrationObj.getRegAddtnInfo().setOrgId(registrationObj.getOrgId());
//			registrationObj.getRegAddtnInfo().setCreatedBy(registrationObj.getOrgCreatedBy());

			registrationObj.setPassword(OrgUtil.getOtp(OrgConstants.OTP_SIZE));
			registrationObj.setIsOTP(OrgConstants.IS_OTP_TRUE);
			registrationObj.setEncryptedPassword(PasswordService.encrypt(registrationObj.getPassword()));
			registrationObj.setOtpExpiryTime(OrgUtil.getOTPExpiryTimestamp());
			regMstrObj = orgDao.fetchScopeOfOrg(registrationObj, conn);
			registrationObj.setUserDfltRole(regMstrObj.getUserDfltRole());
			registrationObj.setScopeOrg(regMstrObj.getScopeOrg());
			registrationObj.setScopeUsr(regMstrObj.getScopeUsr());
			registrationObj.setOrgRenewalStatus(RENEWAL_STATUS.NOT_APPLICABLE);

			if (orgDao.createOrgReg(registrationObj, conn) != 1) {
				response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
				return response;
			}

			registrationObj.setUserId("U" + registrationObj.getOrgId());

			if (orgDao.createDefaultuser(registrationObj, conn) != 1) {
				response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
				return response;
			}

			if (orgDao.insertRegAddtnInfo(registrationObj.getRegAddtnInfo(), conn) != 1) {
				response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
				return response;
			}

			if (OrgUtil.isNeitherNullNorEmpty(registrationObj.getForm5details())) {
				Gson gson = new Gson();
				JsonElement jsonElementArr = gson.toJsonTree(registrationObj.getForm5details());
				FormFiveModel[] formFiveArr = gson.fromJson(jsonElementArr, FormFiveModel[].class);
				for (int i = 0; i < formFiveArr.length; i++) {
					formFiveArr[i].setOrgId(registrationObj.getOrgId());
					formFiveArr[i].setSeqNo(i + 1);
					if (orgDao.insertUpdateForm5Details(formFiveArr[i], conn) != 1) {
						response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
						return response;
					}
				}
			}

			if (commonService.fetchOrgIsMember(registrationObj.getOrgCategory())) {
				MemberMarketMap memberMarketMap = new MemberMarketMap();
				memberMarketMap.setTraderOrgId(registrationObj.getOrgId());
				memberMarketMap.setMarket(registrationObj.getOrgBaseMarket());
				memberMarketMap.setCreatedBy(registrationObj.getOrgId());

				if (registrationObj.isUnifiedLicense()) {
					List<String> marketCodes = new ArrayList<>();
					marketCodes = orgDao.fetchAllMarkets();
					for (int j = 0; j < marketCodes.size(); j++) {
						memberMarketMap.setMarket(marketCodes.get(j));
						memberMarketMap = orgDao.saveMemberMarketMap(memberMarketMap, conn);
					}

				} else {
					memberMarketMap = orgDao.saveMemberMarketMap(memberMarketMap, conn);
				}

				if (!memberMarketMap.getReturnMsg().equalsIgnoreCase(OrgConstants.SUCCESS_STRING)) {
					response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
					return response;
				}

				UserMapping userMarketMap = new UserMapping();
				userMarketMap.setUserId("U" + registrationObj.getOrgId());
				if (registrationObj.isUnifiedLicense()) {
					userMarketMap.setMarketCode("ALL");
				} else {
					userMarketMap.setMarketCode(registrationObj.getOrgBaseMarket());
				}

				userMarketMap.setCreatedBy("U" + registrationObj.getOrgId());
				userMarketMap = orgDao.saveUserMarketMapping(userMarketMap, conn);
				if (!userMarketMap.getReturnMsg().equalsIgnoreCase(OrgConstants.SUCCESS_STRING)) {
					response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
					return response;
				}

				if (!orgDao.createDefaultTraderRole(registrationObj, conn)) {
					response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
					return response;
				}

				registrationObj.setAccType(OrgConstants.ACCOUNT_TYPE.MEMBER);

				if (OrgUtil.isNeitherNullNorEmpty(
						commonService.fetchConfiguration(OrgConstants.CONFIGURATIONS.YES_BANK_CLIENT_ID))) {
					registrationObj.setOrgVirAccNo(
							commonService.fetchConfiguration(OrgConstants.CONFIGURATIONS.YES_BANK_CLIENT_ID)
									+ registrationObj.getOrgId());
				}

				if (registrationObj.isUnifiedLicense()) {
					List<String> marketCodes = new ArrayList<>();
					marketCodes = orgDao.fetchAllMarkets();
					for (int j = 0; j < marketCodes.size(); j++) {
						registrationObj.setOrgBaseMarket(marketCodes.get(j));
						if (!orgDao.checkIfAccMstrExists(registrationObj, conn)) {
							if (orgDao.insertAccountMaster(registrationObj, conn) != 1) {
								response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
							}
						} else {
							if (orgDao.updateAccountMaster(registrationObj, conn) != 1) {
								response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
							}
						}
					}

				} else {
					if (!orgDao.checkIfAccMstrExists(registrationObj, conn)) {
						if (orgDao.insertAccountMaster(registrationObj, conn) != 1) {
							response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
						}
					} else {
						if (orgDao.updateAccountMaster(registrationObj, conn) != 1) {
							response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
						}

					}
				}
			} else {
				if (!orgDao.createDefaultUserRole(registrationObj, conn)) {
					response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
					return response;
				}
			}
			response.setData(registrationObj);
		} catch (Exception e) {
			log.error(OrgConstants.ERROR_CODES.REGISTRATION_FAILED + " | " + e.getMessage());
			response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
			e.printStackTrace();
			return response;

		}
		log.info("Exiting form createOrganisation(RegistrationMaster registrationObj, final Connection conn)::");
		return response;
	}

	@Override
	public String getIsAutoApprovalflag(String orgCategory) {
		log.info("Inside RegistrationServiceImpl => getIsAutoApprovalflag()");
		Connection conn = null;
		String response = null;
		try {
			conn = ds.getConnection();

			response = orgDao.getIsAutoApprovalflag(orgCategory, conn);
		} catch (Exception e) {
			log.error("Error in Database Connction: " + e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		log.info("Exit From RegistrationServiceImpl => getIsAutoApprovalflag()");
		return response;

	}

	// This function checked the organization category is host or not
	// If host then return true otherwise return false
	@Override
	public boolean checkOrgCatIsHost(RegistrationMaster regMaster) {
		log.info("Inside checkOrgCatIsHost() serviceImpl Org Id: " + regMaster.getOrgId() + " Org Category:"
				+ regMaster.getOrgCategory());
		Connection conn = null;
		boolean isHost = false;
		try {
			conn = ds.getConnection();
			isHost = orgDao.checkOrgCatIsHost(regMaster, conn); // function defined below
		} catch (Exception e) {
			log.error("More than one Value found for IS_HOST for this Org Category" + regMaster.getOrgCategory() + "   "
					+ e.getStackTrace() + "   " + e.getMessage());
		} finally {
			
		}
		return isHost;
	}

	// this function will create the organization ID
	public RegistrationMaster createOrgIds(RegistrationMaster registrationObj, final Connection conn) {
		log.info("Inside createOrgIds() serviceImpl " + registrationObj.getOrgId() + " "
				+ registrationObj.getOrgCategory());
		try {
			IdGenerator idObj = new IdGenerator();
			if (registrationObj.isUnifiedLicense()) {
				registrationObj.setOrgBaseMarket("WBMB");
			}

			switch (registrationObj.getOrgCategory()) {
			default:
				idObj = commonService.getIdGenerator(OrgConstants.ORG_CATEGORY_ID_SEQ.REG, conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.RENTER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRADER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.TRADER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCESSOR:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.PROCESSOR + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_COMMISSION_AGENT:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.COMMISSION_AGENT + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BROKER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.BROKER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SURVEYOR:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.SURVEYOR + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WEIGHMAN:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.WEIGHMAN + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MEASURER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.MEASURER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSEMAN:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.WAREHOUSEMAN + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SELLER_PURCHASER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.SELLER_PURCHASER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCURER_PRESERVER:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.PROCURER_PRESERVER + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_STORAGE:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.STORAGE + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HAT:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.HAT + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BAZAR:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.BAZAR + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MELA:
				idObj = commonService.getIdGenerator(
						OrgConstants.ORG_CATEGORY_ID_SEQ.MELA + registrationObj.getOrgBaseMarket(), conn);
				break;

			case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_OTHER_PLACE_OF_SALE_PUR:
				idObj = commonService.getIdGenerator(OrgConstants.ORG_CATEGORY_ID_SEQ.OTHER_PLACE_OF_SALE_PURCHASE
						+ registrationObj.getOrgBaseMarket(), conn);
				break;

			}
			// switch block ends here
			if (OrgConstants.ORG_CATEGORY_STATE_OWNER.equals(registrationObj.getOrgCategory())) {
				String orgId = registrationObj.getOrgState() + "_" + OrgConstants.OWNER_STRING;
				registrationObj.setOrgId(orgId);
			} else {

				String prefixChar = null;

				if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.HST_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.BOARD_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.RMC_OFFICER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_AGENCY.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.AGENCY_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MILLER.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.MILLER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSE.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.WAREHOUSE_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRADER.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.TRADER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCESSOR.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.PROCESSOR_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_COMMISSION_AGENT
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.COMMISSION_AGENT_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BROKER.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.BROKER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SURVEYOR.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.SURVEYOR_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WEIGHMAN.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.WEIGHMAN_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MEASURER.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.MEASURER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSEMAN
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.WAREHOUSEMAN_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCURER_PRESERVER
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.PROCURER_PRESERVER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SELLER_PURCHASER
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.SELLER_PURCHASER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_STORAGE.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.STORAGE_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HAT.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.HAT_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MELA.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.MELA_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BAZAR.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.BAZAR_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PRIVATE_MARKET_APPLICATION
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.PRIVATE_MARKET_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.RENTER_PREFIX_CHARACTER;
				} else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_OTHER_PLACE_OF_SALE_PUR
						.equals(registrationObj.getOrgCategory())) {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.OTHER_PLACE_SALE_PUR_PREFIX_CHARACTER;
				} else {
					prefixChar = OrgConstants.ORG_PREFIX_CHARACTER.NEW_ORG_PREFIX_CHARACTER;
				}

				switch (registrationObj.getOrgCategory()) {
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST:
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD:
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC:
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_AGENCY:
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MILLER:
				case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSE:
				case OrgConstants.ORG_CATEGORY_STATE_OWNER:
					prefixChar += String.format("%05d", idObj.getId().intValue());
					break;
				default:
					prefixChar += (registrationObj.isUnifiedLicense() ? "WBMB" : registrationObj.getOrgBaseMarket())
							+ (registrationObj.isUnifiedLicense() ? "U" : "L")
							+ String.format("%05d", idObj.getId().intValue());
					break;
				}

				registrationObj.setOrgId(prefixChar);
				idObj = null;

			}
			registrationObj.setReturnMsg(OrgConstants.SUCCESS_STRING);
		} catch (Exception e) {
			registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING);
			log.error("Exception - " + e.getMessage());
		}

		log.info("Exiting from createOrgIds() in serviceImpl ");

		return registrationObj;
	}

	@Override
	public RegistrationMaster changeForgotUserpassword(RegistrationMaster registrationObj) {
		log.info("Inside changeForgotUserpassword() serviceImpl " + registrationObj.getOrgId() + "  "
				+ registrationObj.getPassword());
		Connection conn = null;
		String encryptedPassword = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);

			registrationObj.setOrgId(registrationObj.getOrgId());

			encryptedPassword = PasswordService.encrypt(registrationObj.getNewPassword());

			registrationObj.setEncryptedPassword(encryptedPassword);
			registrationObj.setNewPassword(encryptedPassword);

			registrationObj = orgDao.changeUserPassword(registrationObj, conn);
			conn.commit();

			registrationObj.setReturnMsg(OrgConstants.SUCCESS_STRING + "Your password has been changed successfully.");

		} catch (Exception e) {
			registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING + "Error In Changing the password");
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception e1) {
				log.info("Exception in commit or rollback : " + e1.getMessage() + " " + e1.getStackTrace());

			}
			log.error("Exception in changeUserPassword ServiceImpl  " + e.getMessage() + e.getStackTrace());
		} finally {
			
		}
		log.info("Exiting from changeForgotUserpassword() :: password - " + encryptedPassword);
		return registrationObj;

	}

	@Override
	public RegistrationMaster forgotPasswordResendOtp(RegistrationMaster registrationObj) {
		log.info("Inside forgotPasswordResendOtp() ServiceImpl " + registrationObj.getOrgId() + " Password:"
				+ registrationObj.getPassword() + " New Password:" + registrationObj.getNewPassword());
		Connection conn = null;
		try {
			UserMaster userMasterObj = new UserMaster();
			userMasterObj.setUserId(registrationObj.getUserId());
			userMasterObj = orgDao.fetchUserData(userMasterObj);

			conn = ds.getConnection();
			conn.setAutoCommit(false);
			registrationObj.setOrgId(userMasterObj.getOrgId());
			registrationObj.setPassword(OrgUtil.getOtp(OrgConstants.OTP_SIZE));
			registrationObj.setIsOTP(OrgConstants.IS_OTP_TRUE);
			registrationObj.setEncryptedPassword(PasswordService.encrypt(registrationObj.getPassword()));
			registrationObj.setOtpExpiryTime(OrgUtil.getOTPExpiryTimestamp());

			orgDao.updateUserOtp(registrationObj, conn);
			conn.commit();
			registrationObj.setOrgContactMobile(userMasterObj.getUserMobile());
			registrationObj.setReturnMsg(OrgConstants.SUCCESS_STRING + "OTP Resent on Registered Mobile Number");
			this.sendOTPToOrg(registrationObj);

		} catch (Exception e) {
			registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING + "Error In sending Otp");
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception e1) {
				log.info("Exception in commit or rollback : " + e1.getMessage() + " " + e1.getStackTrace());
			}
			log.error("Exception :" + e.getMessage() + " " + e.getStackTrace());
		} finally {
			
		}

		return registrationObj;
	}

	@Override
	public RegistrationMaster checkValidUserOTP(RegistrationMaster registrationObj) {
		log.info("Entering checkValidUserOTP() Service Impl");

		log.info("Start checkValidOTP>>> " + registrationObj.getOrgId() + " OTP:" + registrationObj.getPassword());

		try {
			if (orgDao.checkValidOTPForUser(registrationObj)) {
				registrationObj.setReturnMsg(OrgConstants.SUCCESS_STRING + "Valid OTP. Procceed to Change Password");
			} else {
				registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING + "Invalid OTP entered");
			}

		} catch (Exception e) {
			registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING + "Error In Changing the password");
			log.error("Exception " + e.getMessage() + "  " + e.getStackTrace());
		}
		log.info("Exiting from checkValidUserOTP()");
		return registrationObj;
	}

}
