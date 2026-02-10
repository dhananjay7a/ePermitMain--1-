package com.organisation.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.constants.OrgConstants;
import com.organisation.model.IdGenerator;
import com.organisation.model.MarketMstr;
import com.organisation.model.MessageTracker;
import com.organisation.model.OrgOfficeDetails;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.UserMstr;
import com.organisation.repository.MarketMstrRepository;
import com.organisation.repository.MessageTrackerRepository;
import com.organisation.repository.OfficeDetailsRepository;
import com.organisation.repository.RegisterBasicInfoRepository;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.RegistrationStatusRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.service.EnrollmentService;
import com.organisation.service.PasswordService;
import com.organisation.util.IdGeneratorUtil;
import com.organisation.util.OrgUtil;
import com.register.model.RegisterBasicInfoAddress;
import com.register.model.RegistrationStatus;

import jakarta.transaction.Transactional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

	@Autowired
	private IdGeneratorUtil idGenUtil;

	@Autowired
	private RegistrationMstrRepository regMstrRepo;

	@Autowired
	private UserMstrRepository userMstrRepo;

	@Autowired
	private RegistrationStatusRepository regStatusRepo;

	@Autowired
	private MessageTrackerRepository msgTrackerRepo;

	@Autowired
	private RegisterBasicInfoRepository regBasicInfoRepo;

	@Autowired
	private MarketMstrRepository marketMstrRepo;

	@Autowired
	private OfficeDetailsRepository officeDetailsRepo;

	private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

	@Override
	public RegistrationMaster enrollOrganization(RegistrationMaster registrationObj) {
		log.info("Inside enrollOrganization() :: EnrollmentController");
		RegistrationMstr regMstrObj = null;

		try {
			registrationObj = createOrgIds(registrationObj);

			// Set Registration related status
			RegistrationStatus regStatus = new RegistrationStatus();
			regStatus.setOrgId(registrationObj.getOrgId());
			regStatus.setApplicationStatus(OrgConstants.REGISTRATION_STATUS.ENROLLED);
			regStatus.setActionDateTime(LocalDateTime.now());
			regStatus.setActionTakenBy("SELF");
			regStatus.setRemarks("ENROLLED");
			
			regStatusRepo.save(regStatus);
			
			
			
			regMstrObj = new RegistrationMstr();

			// Insert data into tbl_mst_registration table
			regMstrObj.setOrgId(registrationObj.getOrgId());
			regMstrObj.setUnifiedLicense(registrationObj.isUnifiedLicense());
			regMstrObj.setOrgName(registrationObj.getOrgName());
			

			// set baseMarket based on unified or not - if unified than ALL
			String baseMarket = null;
			if (registrationObj.isUnifiedLicense()) {
				regMstrObj.setOrgBaseMarket("ALL");
				baseMarket = "ALL";
			} else {
				regMstrObj.setOrgBaseMarket(registrationObj.getOrgBaseMarket());
				baseMarket = registrationObj.getOrgBaseMarket();
			}

			String districtCode = null;

			if (registrationObj.isUnifiedLicense()) {
				districtCode = "0";
			} else {
				MarketMstr marketMstr = marketMstrRepo.findByMarketCode(baseMarket)
						.orElseThrow(() -> new RuntimeException("Market Code Not Found"));
				districtCode = marketMstr.getDistrictCode();
			}

			Optional<OrgOfficeDetails> officeDetails = officeDetailsRepo.findByDistrictCodeAndOrgCategory(districtCode,
					registrationObj.getOrgCategory());

			if (officeDetails.isPresent()) {
				regMstrObj.setOrgOfficeCode(officeDetails.get().getOrgOfficeCode());

			} else {
				regMstrObj.setOrgOfficeCode("Default");
			}

			regMstrObj.setOrgMobileNo(registrationObj.getOrgContactMobile());
			//Rahul Pal
			regMstrObj.setOrgCategory(registrationObj.getOrgCategory());
			// regMstrObj.setOrgCategory(registrationObj.getOrgCategory()); -- not required , due to already office code added 
			regMstrObj.setCreatedOn(LocalDateTime.now());
			regMstrObj.setCreatedBy("SELF");
			regMstrRepo.save(regMstrObj);

			// This is for getting the orgCategory in getEnrollDetails - get API
			// RegisterBasicInfoAddress regBasicInfoTemp = new RegisterBasicInfoAddress(); // change
			// regBasicInfoTemp.setOrgId(regMstrObj.getOrgId());
			// regBasicInfoTemp.setUserId("U" + regMstrObj.getOrgId());
			// regBasicInfoTemp.setOrgCategory(regMstrObj.getOrgCategory());
			// regBasicInfoRepo.save(regBasicInfoTemp);

			// Insert this data into the tbl_mst_user_details table
			UserMstr userMstr = new UserMstr();
			userMstr.setOrgId(registrationObj.getOrgId());
			userMstr.setUserId("U" + registrationObj.getOrgId());
			userMstr.setUserMobile(registrationObj.getOrgContactMobile());
			userMstr.setUserEmail(registrationObj.getOrgContactEmail());

			// Task for tomorrow -
			// 1. save it after encryption using PasswordService -- done
			// 2. save it into MessageTracker Table with pending status
			String defaultPassword = OrgUtil.getAlphaNumericOtp(OrgConstants.DEFAULT_PASSWORD_SIZE);
			log.info("Default Password before encryption is : " + defaultPassword);
			
			// log.info("Raihan Raihan - default password is : "+ defaultPassword);
			userMstr.setUserPassword(PasswordService.encrypt(defaultPassword));
			userMstr.setFirstLoginFlag(OrgConstants.YES);
			userMstr.setCreatedOn(LocalDateTime.now());
			userMstr.setCreatedBy("Raihan...");
			userMstr.setUserMobile(registrationObj.getOrgContactMobile());
			userMstrRepo.save(userMstr);

			MessageTracker messageTracker = new MessageTracker();
			messageTracker.setMobileNo(registrationObj.getOrgContactMobile());
			messageTracker.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTracker.setMsgType(OrgConstants.MESSAGE_TYPE.SMS);

			String parameters = registrationObj.getOrgId() + "`" + defaultPassword + "`"
					+ OrgUtil.getOTPExpiryTimestamp();
			messageTracker.setMessage(parameters);

			messageTracker.setStatus("P");
			messageTracker.setRemarks("PENDING");
			messageTracker.setOwner("EPERMIT");
			messageTracker.setCreatedOn(LocalDateTime.now());

			msgTrackerRepo.save(messageTracker);

			

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception : ", e);
		}
		log.info("Exiting from enrollOrganization() :: EnrollmentController");
		return registrationObj;
	}

	@Transactional
	public RegistrationMaster createOrgIds(RegistrationMaster registrationObj) {
		log.info("Inside createOrgIds() EnrollServiceImpl " + registrationObj.getOrgCategory());
		try {
			if (registrationObj.isUnifiedLicense()) {
				registrationObj.setOrgBaseMarket("WBMB");
			}

			String txnTypeKey = this.resolveTxnTypeKey(registrationObj); // put your big switch into a small method that
																			// returns the key (REG, TRADER+WBMB, etc.)
			IdGenerator idObj = idGenUtil.getNextId(txnTypeKey);

			String orgId;
			if (OrgConstants.ORG_CATEGORY_STATE_OWNER.equals(registrationObj.getOrgCategory())) {
				orgId = registrationObj.getOrgState() + "_" + OrgConstants.OWNER_STRING;
			} else {
				String prefix = this.resolvePrefixCharacter(registrationObj.getOrgCategory());
				String idSuffix = String.format("%05d", idObj.getId().intValue());

				if (this.isSpecialOrgCategory(registrationObj.getOrgCategory())) {
					orgId = prefix + idSuffix;
				} else {
					String market = registrationObj.isUnifiedLicense() ? "WBMB" : registrationObj.getOrgBaseMarket();
					String licenseType = registrationObj.isUnifiedLicense() ? "U" : "L";
					orgId = prefix + market + licenseType + idSuffix;
				}
			}

			registrationObj.setOrgId(orgId);
			registrationObj.setReturnMsg(OrgConstants.SUCCESS_STRING);

		} catch (Exception e) {
			registrationObj.setReturnMsg(OrgConstants.FAILURE_STRING);
			log.error("createOrgIds failed", e);
		}
		log.info("Exiting from createOrgIds() EnrollServiceImpl " + registrationObj.getOrgId());
		return registrationObj;
	}

	private String resolveTxnTypeKey(RegistrationMaster regObj) {
		log.info("Inside resolveTxnTypeKey() :: EnrollService - orgCategory : " + regObj.getOrgCategory());
		switch (regObj.getOrgCategory()) {
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.RENTER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRADER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.TRADER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCESSOR:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.PROCESSOR + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_COMMISSION_AGENT:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.COMMISSION_AGENT + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BROKER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.BROKER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SURVEYOR:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.SURVEYOR + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WEIGHMAN:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.WEIGHMAN + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MEASURER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.MEASURER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSEMAN:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.WAREHOUSEMAN + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SELLER_PURCHASER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.SELLER_PURCHASER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCURER_PRESERVER:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.PROCURER_PRESERVER + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_STORAGE:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.STORAGE + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HAT:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.HAT + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BAZAR:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.BAZAR + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MELA:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.MELA + regObj.getOrgBaseMarket();
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_OTHER_PLACE_OF_SALE_PUR:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.OTHER_PLACE_OF_SALE_PURCHASE + regObj.getOrgBaseMarket();
		default:
			return OrgConstants.ORG_CATEGORY_ID_SEQ.REG + regObj.getOrgBaseMarket();
		}
	}

	private String resolvePrefixCharacter(String orgCategory) {
		log.info("Inside resolvePrefixCharacter() :: EnrollService - orgCategory : " + orgCategory);

		switch (orgCategory) {
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST:
			return OrgConstants.ORG_PREFIX_CHARACTER.HST_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD:
			return OrgConstants.ORG_PREFIX_CHARACTER.BOARD_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC:
			return OrgConstants.ORG_PREFIX_CHARACTER.RMC_OFFICER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_AGENCY:
			return OrgConstants.ORG_PREFIX_CHARACTER.AGENCY_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MILLER:
			return OrgConstants.ORG_PREFIX_CHARACTER.MILLER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSE:
			return OrgConstants.ORG_PREFIX_CHARACTER.WAREHOUSE_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRADER:
			return OrgConstants.ORG_PREFIX_CHARACTER.TRADER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCESSOR:
			return OrgConstants.ORG_PREFIX_CHARACTER.PROCESSOR_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_COMMISSION_AGENT:
			return OrgConstants.ORG_PREFIX_CHARACTER.COMMISSION_AGENT_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BROKER:
			return OrgConstants.ORG_PREFIX_CHARACTER.BROKER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SURVEYOR:
			return OrgConstants.ORG_PREFIX_CHARACTER.SURVEYOR_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WEIGHMAN:
			return OrgConstants.ORG_PREFIX_CHARACTER.WEIGHMAN_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MEASURER:
			return OrgConstants.ORG_PREFIX_CHARACTER.MEASURER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSEMAN:
			return OrgConstants.ORG_PREFIX_CHARACTER.WAREHOUSEMAN_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCURER_PRESERVER:
			return OrgConstants.ORG_PREFIX_CHARACTER.PROCURER_PRESERVER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SELLER_PURCHASER:
			return OrgConstants.ORG_PREFIX_CHARACTER.SELLER_PURCHASER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_STORAGE:
			return OrgConstants.ORG_PREFIX_CHARACTER.STORAGE_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HAT:
			return OrgConstants.ORG_PREFIX_CHARACTER.HAT_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MELA:
			return OrgConstants.ORG_PREFIX_CHARACTER.MELA_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BAZAR:
			return OrgConstants.ORG_PREFIX_CHARACTER.BAZAR_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PRIVATE_MARKET_APPLICATION:
			return OrgConstants.ORG_PREFIX_CHARACTER.PRIVATE_MARKET_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER:
			return OrgConstants.ORG_PREFIX_CHARACTER.RENTER_PREFIX_CHARACTER;
		case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_OTHER_PLACE_OF_SALE_PUR:
			return OrgConstants.ORG_PREFIX_CHARACTER.OTHER_PLACE_SALE_PUR_PREFIX_CHARACTER;
		default:
			return OrgConstants.ORG_PREFIX_CHARACTER.NEW_ORG_PREFIX_CHARACTER;
		}
	}

	private boolean isSpecialOrgCategory(String orgCategory) {
		log.info("Inside isSpecialOrgCategory() :: EnrollService - orgCategory : " + orgCategory);
		return orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST
				|| orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD
				|| orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC
				|| orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_AGENCY
				|| orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MILLER
				|| orgCategory == OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSE
				|| orgCategory == OrgConstants.ORG_CATEGORY_STATE_OWNER;
	}

}
