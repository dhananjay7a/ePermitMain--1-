package com.organisation.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epermit.Exception.FeeAlreadyPaidException;
import com.epermit.Exception.FeeNotPaidException;
import com.epermit.register.dto.AdditionalDetailsDTO;
import com.epermit.register.dto.BankGstDTO;
import com.epermit.register.dto.BasicInfoDTO;
import com.epermit.register.dto.ChangePasswordDTO;
import com.epermit.register.dto.DocumentDTO;
import com.epermit.register.dto.DocumentUploadDTO;
import com.epermit.register.dto.FinalRegistrationFormDTO;
import com.epermit.register.dto.LicenseDetailsDTO;
import com.epermit.register.dto.PermanentAddressDTO;
import com.epermit.register.dto.RegDetailsResponseDTO;
import com.epermit.register.dto.RegisterBusinessAddressDTO;
import com.epermit.register.dto.RegistrationPreviewDTO;
import com.epermit.register.dto.ScrutinyRequestDTO;
import com.epermit.register.dto.TermsAndConditionsDTO;
import com.epermit.register.dto.TermsAndConditionsResponseDTO;
import com.epermit.register.dto.UserDigiSignRequestDto;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.constants.OrgConstants;
import com.organisation.constants.OrgConstants.REGISTRATION_STATUS;
import com.organisation.dao.RegistrationDao;
import com.organisation.dto.ChangeDefaultPasswordDTO;
import com.organisation.dto.OtpValidateDTO;
import com.organisation.model.AccountMstr;
import com.organisation.model.BankGstMstr;
import com.organisation.model.DropDownMaster;
import com.organisation.model.LicneseMstr;
import com.organisation.model.MarketMstr;
import com.organisation.model.MessageTracker;
import com.organisation.model.OrgCategoryMaster;
import com.organisation.model.OrgOfficeDetails;
import com.organisation.model.RegisterAdditionalDetails;
import com.organisation.model.RegisterBusinessAddressDetails;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.ResponseMessage;
import com.organisation.model.TermsAndConditions;
import com.organisation.model.UserDigiSignEntity;
import com.organisation.model.UserMstr;
import com.organisation.repository.AccountMstrRepository;
import com.organisation.repository.BankGstDetailsRepository;
import com.organisation.repository.BankGstMstrRepository;
import com.organisation.repository.DocumentUploadTempRepository;
import com.organisation.repository.LicenseMstrRepository;
import com.organisation.repository.LicenseeDetailsRepository;
import com.organisation.repository.MarketMstrRepository;
import com.organisation.repository.MessageTrackerRepository;
import com.organisation.repository.OfficeDetailsRepository;
import com.organisation.repository.OrgCategoryRepository;
import com.organisation.repository.RegisterAdditionalDetailsRepository;
import com.organisation.repository.RegisterAdditionalDetailsTempRepository;
import com.organisation.repository.RegisterBasicInfoRepository;
import com.organisation.repository.RegisterBusinessAddressFinalRepository;
import com.organisation.repository.RegisterBusinessAddressRepository;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.RegistrationStatusRepository;
import com.organisation.repository.TermsAndConditionsRepository;
import com.organisation.repository.UserDigiSignRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.security.TokenService;
import com.organisation.service.PasswordService;
import com.organisation.service.RegistrationService;

import com.organisation.util.OrgUtil;
import com.register.model.BankGstDetails;
import com.register.model.DocumentUploadTemp;
import com.register.model.LicenseeDetailsTemp;
import com.register.model.RegisterAdditionalDetailsTemp;
import com.register.model.RegisterBasicInfo;
import com.register.model.RegisterBasicInfoAddress;
import com.register.model.RegisterBusinessAddress;
import com.register.model.RegisterBusinessAddressId;
import com.register.model.RegistrationStatus;

import io.jsonwebtoken.Claims;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	// private final PublicController publicController;

	@Value("${file.upload.temp-path}")
	private String uploadRootFolder;

	@Autowired
	private DocumentUploadTempRepository documentUploadRepo;

	@Autowired
	private RegistrationDao registrationDao;

	@Autowired
	private RegisterBasicInfoRepository basicInfoRepo;

	@Autowired
	private RegisterBusinessAddressRepository businessAddressRepo;

	@Autowired
	private RegisterAdditionalDetailsTempRepository additionalDetailsTempRepo;

	@Autowired
	private BankGstDetailsRepository bankRepo;

	@Autowired
	private LicenseeDetailsRepository licenseRepo;

	@Autowired
	private UserMstrRepository userMstrRepo;

	@Autowired
	private RegistrationMstrRepository regMstrRepo;

	@Autowired
	private RegisterBusinessAddressFinalRepository businessRepo;

	@Autowired
	private LicenseMstrRepository licenseMstrRepo;

	@Autowired
	private BankGstMstrRepository bankMstrRepo;

	@Autowired
	private RegisterAdditionalDetailsRepository additionalDetailsRepo;

	// @Autowired
	// private OfficeDetailsRepository officeDetailsRepo;

	@Autowired
	private TermsAndConditionsRepository termsRepo;

	@Autowired
	private MarketMstrRepository marketMstrRepository;

	@Autowired
	private OrgCategoryRepository orgCategoryRepo;

	@Autowired
	private MessageTrackerRepository msgTrackerRepo;

	@Autowired
	private RegistrationStatusRepository regStatusRepo;

	@Autowired
	private RegisterBasicInfoRepository regBasicInfoRepo;

	@Autowired
	private AccountMstrRepository accMstrRepo;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private TokenService ts;

	@Autowired
	private UserDigiSignRepository dsignRepo;

	private static final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Override
	public void getEnrollDetails(RegistrationMstr regMaster, String token, ResponseBean responseBean) {
		try {

			log.info("Inside getEnrollDetails() :: RegServiceImpl");

			// 👉 Remove Bearer prefix
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔒 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			String userId = ts.extractUserId(token);

			String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

			// 🎯 Fetch DB data
			RegistrationMstr fetchedRegMaster = registrationDao.findRegDetailsByOrgId(regMaster.getOrgId());

			if (fetchedRegMaster == null) {
				responseBean.AllResponse("NoDataFound", null);
				return;
			}

			// ✅ Build response DTO
			RegDetailsResponseDTO dto = new RegDetailsResponseDTO();
			dto.setOrgId(fetchedRegMaster.getOrgId());
			dto.setOrgName(fetchedRegMaster.getOrgName());
			dto.setUnifiedLicense(fetchedRegMaster.isUnifiedLicense());
			dto.setOrgCategory(fetchedRegMaster.getOrgCategory());

			// 🔐 Conditional field
			if (!fetchedRegMaster.isUnifiedLicense()) {
				dto.setOrgBaseMarket(fetchedRegMaster.getOrgBaseMarket());
			}

			responseBean.AllResponse("Success", dto);

			log.info("Exit from getEnrollDetails() :: RegServiceImpl");
		} catch (Exception e) {
			responseBean.AllResponse("Error", null);
		}
	}

	@Override
	public Map<String, Object> getEnrollDetails(String orgId) {
		log.info("Inside getRegDetails() :: RegServiceImpl for orgId = {}", orgId);
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<RegistrationMstr> regMstrOpt = regMstrRepo.findByOrgId(orgId);
			Optional<RegistrationStatus> regStatusOpt = regStatusRepo.findByOrgId(orgId);
			Optional<RegisterBasicInfoAddress> regBasicInfoOpt = regBasicInfoRepo.findByOrgId(orgId);

			regMstrOpt.ifPresent(regMstr -> {
				response.put("orgId", regMstr.getOrgId()); // also you can directly write orgId - getting from the
															// parameter
				response.put("orgName", regMstr.getOrgName());
				response.put("isUnified", regMstr.isUnifiedLicense());
				response.put("regFeeValidity", regMstr.getRegFeeValidity());
				response.put("orgBaseMarket", regMstr.getOrgBaseMarket());
			});

			regStatusOpt.ifPresent(regStatus -> {
				response.put("orgRequestStatus", regStatus.getApplicationStatus());
			});

			regBasicInfoOpt.ifPresent(regBasic -> {
				response.put("orgCategory", regBasic.getOrgCategory());
			});

		} catch (Exception e) {
			log.error("Error in getRegDetails for orgId = {}: {}", orgId, e.getMessage(), e);
		}
		log.info("Exiting from getRegDetails() :: RegServiceImpl");
		return response;
	}

	@Override
	public List<OrgCategoryMaster> getOrgTypeForSignUp() {
		log.info("Inside getOrgTypeForSignUp():: RegServiceImpl");
		// Categories to exclude
		List<String> excludedCategories = List.of(OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST,
				OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC, OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD,
				OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER);

		List<OrgCategoryMaster> categoryEntities = orgCategoryRepo.findByOrgCategoryNotIn(excludedCategories);

		// List<DropDownMaster> dropDownList = new ArrayList<>();
		// for (OrgCategoryMaster entity : categoryEntities) {
		// DropDownMaster dropDown = new DropDownMaster();
		// dropDown.setRoleId(entity.getOrgCategory());
		// dropDown.setValue(entity.getOrgCategoryName());
		// dropDown.setOrgCategoryScope(entity.getOrgCategoryScope());
		// dropDownList.add(dropDown);
		// }
		//
		log.info("Exiting getOrgTypeForSignUp():: RegServiceImpl");
		return categoryEntities;

	}

	@Override
	public List<MarketMstr> fetchAllMarketsPublic(MarketMstr marketObject) {
		log.info("Inside fetchAllMarketPublic() :: RegServiceImpl");
		try {
			return marketMstrRepository.findAllByIsMockMarketAndIsDenotifiedMarket("N", "N");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception : ", e);
		}
		return Collections.emptyList();

	}

	// Updated by _____ Raihan
	@Override
	public String createOTP(MessageTracker messageTracker) {
		log.info("Inside createOTP():: OrgServiceImpl");
		String otp = OrgUtil.getOtp(OrgConstants.OTP_SIZE);
		try {
			String msg_parameters = "NEW_USER" + "`" + otp + "`" + OrgUtil.getOTPExpiryTimestamp();

			messageTracker.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTracker.setMsgType("S");
			messageTracker.setMessage(msg_parameters);
			messageTracker.setStatus("P");
			messageTracker.setRemarks("PENDING");
			messageTracker.setOwner("EPERMIT");
			messageTracker.setCreatedOn(LocalDateTime.now());

			MessageTracker saved = msgTrackerRepo.save(messageTracker);
			log.info("OTP saved with ID: {}", saved.getMsgId());

			return otp;

		} catch (Exception e) {
			log.error("Error in createOTP(): {}", e.getMessage(), e);
			return "Failed to generate OTP.";
		} finally {
			log.info("Exit from createOTP():: OrgServiceImpl");
		}
	}

	// Updated by -- Raihan
	@Override
	public boolean verifyOTP(String mobileNo, String inputOtp) {
		log.info("Inside verifyOTP() for mobileNo: {}", mobileNo);

		Optional<MessageTracker> latestOtpRecord = msgTrackerRepo.findTopByMobileNoOrderByCreatedOnDesc(mobileNo);
		if (latestOtpRecord.isPresent()) {
			MessageTracker tracker = latestOtpRecord.get();
			String[] parts = tracker.getMessage().split("`");
			String savedOTP = parts[1];
			String expiryTime = parts[2];

			if (savedOTP.equals(inputOtp)) {
				// check expired or not
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
				if (LocalDateTime.now().isBefore(LocalDateTime.parse(expiryTime, formatter))) {
					log.info("OTP verified successfully for mobileNo : {}", mobileNo);
					return true;
				} else {
					log.warn("OTP expired for mobileNo : {}", mobileNo);
					return false;
				}
			} else {
				log.warn("Invalid OTP send for the mobileNo : {}", mobileNo);
				return false;
			}
		} else {
			log.warn("No OTP found for the mobileNo : {}", mobileNo);
			return false;
		}
	}

	@Override
	public ResponseMessage editBasicInfo(RegisterBasicInfo basicInfoObj) {
		Connection conn = null;
		ResponseMessage response = new ResponseMessage();
		log.info("Inside editBasicInfo() serviceImpl " + basicInfoObj.getOrgId() + " " + basicInfoObj.getOrgName());
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			response = this.editBasicInfo(basicInfoObj, conn);

		} catch (Exception ex) {
			response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
			log.error("Exception : " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (response.getErrorCode() == OrgConstants.ERROR_CODES.NO_ERROR) {
					conn.commit();
				} else {
					if (conn != null)
						conn.rollback();
				}
			} catch (Exception e1) {
				log.error(e1.getMessage());
				e1.printStackTrace();
			}

		}
		log.info("Exiting editBasicInfo() :: RegServiceImpl");
		return response;
	}

	private ResponseMessage editBasicInfo(RegisterBasicInfo basicInfoObj, Connection conn) {
		log.info("Inside editBasicInfo() serviceImpl " + basicInfoObj.getOrgId() + " " + basicInfoObj.getUserId() + " "
				+ basicInfoObj.isUnifiedLicense());

		ResponseMessage response = new ResponseMessage();
		try {

			basicInfoObj.setUpdatedBy(basicInfoObj.getUserId());
			basicInfoObj.setUserId(basicInfoObj.getUserId());
			if (!registrationDao.saveDraftBasicInfo(basicInfoObj, conn)) {
				response.setErrorCode(OrgConstants.ERROR_CODES.REGISTRATION_FAILED);
				response.setMessage("Registration failed.");
			} else {
				response.setErrorCode(OrgConstants.ERROR_CODES.NO_ERROR);
				response.setMessage("Basic info successfully added.");
			}
			response.setMessage("Basic Info Successfully added");
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
		return response;
	}

	// this is just testing purpose
	@Override
	public void saveBasicInfo(BasicInfoDTO dto, String token) {
		try {
			// 🔐 remove Bearer prefix if present
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🚨 check token validity
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// 🆔 extract userid from token
			String userId = ts.extractUserId(token);
			dto.setUserId(userId);

			// 💾 Save entity
			RegisterBasicInfoAddress entity = new RegisterBasicInfoAddress();
			entity.setUserId(dto.getUserId());
			entity.setOrgId(dto.getOrgId());
			entity.setUnifiedLicense(dto.isUnifiedLicense());
			entity.setOrgCategory(dto.getOrgCategory());
			entity.setOrgName(dto.getOrgName());
			entity.setOrgType(dto.getOrgType());
			entity.setOrgConsttnDate(dto.getOrgConsttnDate());
			entity.setOrgBaseMarket(dto.getOrgBaseMarket());
			entity.setOrgDocType(dto.getOrgDocType());
			entity.setOrgDocNo(dto.getOrgDocNo());
			entity.setHasExistingLicense(dto.isHasExistingLicense());
			entity.setOrgCreatedBy(userId);
			entity.setOrgCreatedOn(LocalDateTime.now());

			basicInfoRepo.save(entity);

		} catch (Exception e) {
			log.error("Error occurred while saving basic info: " + e.getMessage());
			throw new RuntimeException("Failed to save basic info", e);
		}
	}

	// This is for Testing
	@Override
	public void savePermanentAndBusinessAddress(PermanentAddressDTO dto, String token) {
		try {

			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			String userId = ts.extractUserId(token); // e.g., UEPUMDL00007

			// Convert userId → orgId (remove leading 'U')
			String orgId = userId.startsWith("U") ? userId.substring(1) : userId; // e.g., EPUMDL00007

			// Set in DTO
			dto.setOrgId(orgId);

			log.info("Inside savePermanentAndBusinessAddress() :: OrgId={}", dto.getOrgId());

			// Step 1: Fetch basic info using orgId
			RegisterBasicInfoAddress entity = basicInfoRepo.findByOrgId(dto.getOrgId())
					.orElseThrow(() -> new RuntimeException("User with orgId " + dto.getOrgId() + " not found"));

			// Step 2: Save Permanent Address
			entity.setOrgApplicantName(dto.getOrgApplicantName());
			entity.setOrgApplicantParentName(dto.getOrgApplicantParentName());
			entity.setOrgAddress(dto.getOrgAddress());
			entity.setOrgState(dto.getOrgState());
			entity.setOrgCity(dto.getOrgCity());
			entity.setOrgDist(dto.getOrgDist());
			entity.setOrgPoliceStation(dto.getOrgPoliceStation());
			entity.setOrgPostOffice(dto.getOrgPostOffice());
			entity.setOrgMobileNo(dto.getOrgMobileNo());
			entity.setOrgPin(dto.getOrgPin());
			entity.setOrgBlockName(dto.getOrgBlockName());
			entity.setAddressCreatedBy(userId);
			entity.setAddressModifiedBy(userId);
			entity.setAddressCreatedOn(LocalDateTime.now());
			entity.setAddressModifiedOn(LocalDateTime.now());

			basicInfoRepo.save(entity);

			// Step 3: Save Business Addresses
			List<RegisterBusinessAddress> businessEntities = new ArrayList<>();
			for (RegisterBusinessAddressDTO b : dto.getBusinessAddresses()) {

				RegisterBusinessAddress businessAddress = new RegisterBusinessAddress();

				// 👉 Always use orgId extracted from token
				RegisterBusinessAddressId id = new RegisterBusinessAddressId();
				id.setOrgId(orgId);
				id.setOrgBaseMarket(b.getOrgBaseMarket());
				businessAddress.setId(id);

				businessAddress.setOrgMarketAddress(b.getOrgMarketAddress());
				businessAddress.setOrgMarketState(b.getOrgMarketState());
				businessAddress.setOrgMarketCity(b.getOrgMarketCity());
				businessAddress.setOrgMarketDist(b.getOrgMarketDist());
				businessAddress.setOrgMarketPoliceStation(b.getOrgMarketPoliceStation());
				businessAddress.setOrgMarketPostOffice(b.getOrgMarketPostOffice());
				businessAddress.setOrgMarketPhoneNo(b.getOrgMarketPhoneNo());
				businessAddress.setOrgMarketPin(b.getOrgMarketPin());
				businessAddress.setOrgMarketBlockName(b.getOrgMarketBlockName());
				businessAddress.setOrgCreatedBy(userId);
				businessAddress.setOrgCreatedOn(LocalDateTime.now());
				businessAddress.setOrgModifiedBy(userId);
				businessAddress.setOrgModifiedOn(LocalDateTime.now());

				businessEntities.add(businessAddress);
			}

			businessAddressRepo.saveAll(businessEntities);

		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException("Error while saving address info: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void savePermanentAddress(String userId, PermanentAddressDTO dto) {
		try {
			RegisterBasicInfoAddress entity = basicInfoRepo.findById(userId)
					.orElseThrow(() -> new RuntimeException("User with userId " + userId + " not found"));

			entity.setOrgAddress(dto.getOrgAddress());
			entity.setOrgState(dto.getOrgState());
			entity.setOrgCity(dto.getOrgCity());
			entity.setOrgDist(dto.getOrgDist());
			entity.setOrgPoliceStation(dto.getOrgPoliceStation());
			entity.setOrgPostOffice(dto.getOrgPostOffice());
			entity.setOrgMobileNo(dto.getOrgMobileNo());
			entity.setOrgPin(dto.getOrgPin());
			entity.setOrgBlockName(dto.getOrgBlockName());
			// entity.setAddressType(dto.getAddressType());

			entity.setAddressCreatedBy(dto.getAddressCreatedBy());
			entity.setAddressCreatedOn(LocalDateTime.now());
			entity.setAddressModifiedBy(dto.getAddressCreatedBy());
			entity.setAddressModifiedOn(LocalDateTime.now());

			basicInfoRepo.save(entity);

		} catch (RuntimeException e) {
			log.error("Runtime error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while saving permanent address: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to save permanent address", e);
		}
	}

	@Override
	public void saveAdditionalDetails(AdditionalDetailsDTO dto, String token) {
		try {
			// 👉 Remove Bearer prefix
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔒 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// 🆔 Extract userId : example UEPUMDL00007
			String userId = ts.extractUserId(token);

			// 🏢 Convert to orgId : EPUMDL00007
			String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

			// 🏗 Build entity
			RegisterAdditionalDetailsTemp entity = new RegisterAdditionalDetailsTemp();
			entity.setOrgId(orgId);
			entity.setIsBGReady(dto.getIsBGReady());
			entity.setCommodityCode(dto.getCommodityCode());
			entity.setLandDetails(dto.getLandDetails());
			entity.setIsRegistered(dto.getIsRegistered());
			entity.setRegisteredWith(dto.getRegisteredWith());
			entity.setRegistrationDetails(dto.getRegistrationDetails());
			entity.setMarketYearApp(dto.getMarketYearApp());
			entity.setOrgValidFrom(dto.getOrgValidFrom());
			entity.setRegFeeValidity(dto.getRegFeeValidity());
			entity.setOrgContactPerson(dto.getOrgContactPerson());
			entity.setImpExp(dto.getImpExp());
			entity.setImpExpCommodities(dto.getImpExpCommodities());
			entity.setAmenitiesDetails(dto.getAmenitiesDetails());
			entity.setTransactionDetails(dto.getTransactionDetails());
			entity.setHasPreviousExp(dto.getHasPreviousExp());
			entity.setPrevExpRemarks(dto.getPrevExpRemarks());
			entity.setHadLicense(dto.getHadLicense());
			entity.setPrevLicenseRemarks(dto.getPrevLicenseRemarks());
			entity.setHadMarketLicense(dto.getHadMarketLicense());
			entity.setPrevMarketLicenseRemarks(dto.getPrevMarketLicenseRemarks());
			entity.setIsGuilty(dto.getIsGuilty());
			entity.setGuiltyRemarks(dto.getGuiltyRemarks());
			entity.setGodownDetailsRemarks(dto.getGodownDetailsRemarks());
			entity.setPrivateMarketYardArea(dto.getPrivateMarketYardArea());
			entity.setStyleForPrivateMarketYard(dto.getStyleForPrivateMarketYard());
			entity.setSituationPrivateMarketYard(dto.getSituationPrivateMarketYard());
			entity.setNatureOfInterestOnLand(dto.getNatureOfInterestOnLand());
			entity.setEmployeeDetails(dto.getEmployeeDetails());
			entity.setOfficial1(dto.getOfficial1());
			entity.setOfficial1Father(dto.getOfficial1Father());
			entity.setOfficial2(dto.getOfficial2());
			entity.setOfficial2Father(dto.getOfficial2Father());
			entity.setOfficial3(dto.getOfficial3());
			entity.setOfficial3Father(dto.getOfficial3Father());

			// 🕒 Audit trail
			entity.setCreatedBy(userId);
			entity.setModifiedBy(userId);

			additionalDetailsTempRepo.save(entity);

		} catch (Exception e) {
			throw new RuntimeException("Unable to save additional details: " + e.getMessage());
		}
	}

	@Override
	public DocumentUploadTemp saveFile(String token, String docType, MultipartFile file) throws IOException {

		// 👉 Remove Bearer prefix
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		// 🔒 Validate Token
		Claims claims = ts.validateToken(token);
		if (claims == null) {
			throw new RuntimeException("Invalid Token");
		}

		// 🆔 Extract User ID (Example: UEPUMDL00007)
		String userId = ts.extractUserId(token);

		// 🏢 Convert to Org ID (EPUMDL00007)
		String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

		log.info("Inside saveFile() :: RegServiceImpl - orgId : " + orgId);

		// 📂 Get file extension & build file name
		String fileExtension = this.getExtension(file.getOriginalFilename());
		String fileName = docType + "." + fileExtension;

		// 📁 Create Org Folder if not exists
		File orgFolder = new File(uploadRootFolder + File.separator + orgId);
		if (!orgFolder.exists()) {
			orgFolder.mkdirs();
		}

		// 📤 Destination Path
		File destination = new File(orgFolder, fileName);

		// 💾 Save File to Disk
		file.transferTo(destination);

		// 🧾 Check if Document Exists
		Optional<DocumentUploadTemp> existingDoc = documentUploadRepo.findByOrgIdAndDocType(orgId, docType);
		DocumentUploadTemp doc;

		if (existingDoc.isPresent()) {
			// 🔄 Update Existing File Record
			doc = existingDoc.get();
			doc.setModifiedOn(LocalDateTime.now());
			doc.setModifiedBy(userId); // 👈 store USERID

		} else {
			// 🆕 Create New Upload Record
			doc = new DocumentUploadTemp();
			doc.setOrgId(orgId);
			doc.setDocType(docType);
			doc.setCreatedOn(LocalDateTime.now());
			doc.setCreatedBy(userId); // 👈 store USERID
		}

		// 📌 Common fields
		doc.setDocName(fileName);
		doc.setUploadPath(destination.getAbsolutePath());

		log.info("Exiting from saveFile() :: RegServiceImpl");
		return documentUploadRepo.save(doc);
	}

	@Override
	public ResponseMessage checkValidFile(MultipartFile file) {
		log.info("Inside checkValidFile()");

		ResponseMessage response = new ResponseMessage();
		String extension = getExtension(file.getOriginalFilename());

		if (extension != null) {
			extension = extension.toLowerCase();
		}

		try {
			byte[] fileBytes = file.getBytes();

			switch (extension) {
				case "jpg":
				case "jpeg":
					if (!isValidJPEG(fileBytes)) {
						response.setErrorCode(OrgConstants.ERROR_CODES.FILE_FORMAT_MISMATCH);
						response.setMessage("Invalid JPEG file");
						return response;
					}
					break;

				case "png":
					if (!isValidPNG(fileBytes)) {
						response.setErrorCode(OrgConstants.ERROR_CODES.FILE_FORMAT_MISMATCH);
						response.setMessage("Invalid PNG file");
						return response;
					}
					break;

				case "pdf":
					if (!isValidPDF(fileBytes)) {
						response.setErrorCode(OrgConstants.ERROR_CODES.FILE_FORMAT_MISMATCH);
						response.setMessage("Invalid PDF file");
						return response;
					}
					break;

				default:
					response.setErrorCode(OrgConstants.ERROR_CODES.FILE_FORMAT_MISMATCH);
					response.setMessage("Unsupported file type: " + extension);
					return response;
			}

		} catch (IOException e) {
			log.error("Exception in checkValidFile(): " + e.getMessage());
			response.setErrorCode(OrgConstants.ERROR_CODES.FILE_FORMAT_MISMATCH);
			response.setMessage("Unable to read file for validation");
			return response;
		}

		response.setErrorCode(OrgConstants.ERROR_CODES.NO_ERROR);
		response.setMessage("File is valid");
		log.info("Exiting checkValidFile()");
		return response;
	}

	// private String getExtension(String filename) {
	// if (filename != null && filename.contains(".")) {
	// return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
	// }
	// return null;
	// }

	private String getExtension(String filename) {
		log.info("Inside getExtension() :: RegServiceImpl");
		try {
			if (filename == null || filename.trim().isEmpty()) {
				return null;
			}

			int lastDotIndex = filename.lastIndexOf('.');

			// Check if '.' exists and it's not the first or last character
			if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
				return filename.substring(lastDotIndex + 1).toLowerCase();
			}
		} catch (Exception e) {
			log.error("Error while extracting file extension: " + e.getMessage());
		}

		return null;
	}

	private boolean isValidJPEG(byte[] bytes) {
		return bytes.length > 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF;
	}

	private boolean isValidPNG(byte[] bytes) {
		byte[] pngSignature = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
		if (bytes.length < pngSignature.length)
			return false;
		for (int i = 0; i < pngSignature.length; i++) {
			if (bytes[i] != pngSignature[i])
				return false;
		}
		return true;
	}

	private boolean isValidPDF(byte[] bytes) {
		byte[] pdfSignature = new byte[] { 0x25, 0x50, 0x44, 0x46 }; // %PDF
		if (bytes.length < pdfSignature.length)
			return false;
		for (int i = 0; i < pdfSignature.length; i++) {
			if (bytes[i] != pdfSignature[i])
				return false;
		}
		return true;
	}

	@Override
	public void saveBankGstDetails(BankGstDTO dto, String token) {
		try {
			// 👉 Remove Bearer prefix
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔒 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// 🆔 Extract userId (UEPUMDL00007)
			String userId = ts.extractUserId(token);

			// 🔁 Convert to orgId (EPUMDL00007)
			String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

			// 🏦 Save GST + Bank Info
			BankGstDetails entity = new BankGstDetails();
			entity.setOrgId(orgId);
			entity.setOrgGstNo(dto.getOrgGstNo());
			entity.setOrgGstState(dto.getOrgGstState());
			entity.setOrgBankName(dto.getOrgBankName());
			entity.setOrgBankAccNo(dto.getOrgBankAccNo());
			entity.setOrgIfscCode(dto.getOrgIfscCode());
			entity.setOrgBankBranch(dto.getOrgBankBranch());

			// 🕒 Audit Fields
			entity.setOrgCreatedBy(userId);
			entity.setOrgCreatedOn(LocalDateTime.now());
			entity.setOrgModifiedBy(userId);
			entity.setOrgModifiedOn(LocalDateTime.now());

			bankRepo.save(entity);

		} catch (Exception e) {
			throw new RuntimeException("Failed to save Bank and GST details: " + e.getMessage());
		}
	}

	@Override
	public LicenseeDetailsTemp saveLicenseeDetails(LicenseeDetailsTemp details, String token) {
		log.info("Inside saveLicenseeDetails() :: orgId={}", details.getOrgId());

		try {
			// ⚡ Remove Bearer prefix
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔥 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token"); // 👈 Will be caught in Controller
			}

			// String userId = ts.extractUserId(token);
			// details.setUserId(userId);
			// Check if a record with the same orgId already exists
			Optional<LicenseeDetailsTemp> existing = licenseRepo.findById(details.getOrgId());

			if (existing.isPresent()) {
				log.info("Existing licensee found for orgId={} — Updating record", details.getOrgId());
				// Optionally update only specific fields
				LicenseeDetailsTemp toUpdate = existing.get();
				toUpdate.setLicenseNo(details.getLicenseNo());
				toUpdate.setLicenseBookNo(details.getLicenseBookNo());
				toUpdate.setLicenseReceiptBookNo(details.getLicenseReceiptBookNo());
				toUpdate.setLicenseReceiptNo(details.getLicenseReceiptNo());
				toUpdate.setOrgValidFrom(details.getOrgValidFrom());
				toUpdate.setRegFeeValidity(details.getRegFeeValidity());

				return licenseRepo.save(toUpdate);
			} else {
				log.info("No existing licensee for orgId={} — Creating new record", details.getOrgId());
				return licenseRepo.save(details);
			}

		} catch (Exception e) {
			log.error("Exception in saveLicenseeDetails(): {}", e.getMessage(), e);
			throw new RuntimeException("Failed to save licensee details: " + e.getMessage());
		}
	}

	@Override
	public boolean checkMandatoryTerms(String formType, int totalMandatoryCount, String token) {
		try {
			// 👉 Remove Bearer prefix
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔒 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// 🔐 Extract userId only for tracking (optional use)
			String userId = ts.extractUserId(token);
			log.info("Checking terms by user: {}", userId);

			log.info("Inside checkMandatoryTerms() :: RegServiceImpl");

			// 🧮 Core Logic: Check mandatory count
			int dbMandatoryCount = termsRepo.countByFormTypeAndIsMandatoryTrue(formType);

			return dbMandatoryCount == totalMandatoryCount;

		} catch (RuntimeException ex) {
			throw ex; // handled in controller
		} catch (Exception ex) {
			log.error("Error while checking mandatory terms for formType {}: {}", formType, ex.getMessage(), ex);
			return false;
		}
	}

	// final
	public void saveFinalSubmissionTest(FinalRegistrationFormDTO form) throws IOException {
		log.info("Inside saveFinalSubmission():: RegServiceImpl");
		LocalDateTime now = LocalDateTime.now();
		// Timestamp tsNow = Timestamp.valueOf(now);

		try {
			BasicInfoDTO basic = form.getBasicInfo();
			if (basic == null) {
				throw new IllegalArgumentException("basicInfo is missing in request.");
			}

			PermanentAddressDTO address = form.getPermanentAddress();
			if (address == null) {
				throw new IllegalArgumentException("permanentAddress is missing in request.");
			}
			LicenseDetailsDTO license = null;
			if (basic.isHasExistingLicense()) {
				license = form.getLicenseDetails();
			}
			BankGstDTO bankGst = form.getBankGst();
			RegisterAdditionalDetails additionalDetails = form.getAdditional();

			// Change the status from Enrolled to Saved
			RegistrationStatus regStatus = new RegistrationStatus();
			regStatus.setOrgId(basic.getOrgId());
			regStatus.setApplicationStatus(OrgConstants.REGISTRATION_STATUS.SAVED);
			regStatus.setActionDateTime(LocalDateTime.now());
			regStatus.setActionTakenBy("SELF");
			regStatus.setRemarks("SAVED");

			regStatusRepo.save(regStatus);

			// 1. Save tbl_mst_user_details
			// UserMstr user = new UserMstr();
			UserMstr user = userMstrRepo.findByUserId(basic.getUserId());
			if (user == null) {
				throw new IllegalArgumentException("User not found");
			}
			user.setUserId(basic.getUserId());
			user.setOrgId(basic.getOrgId());
			user.setUserName(basic.getOrgName());
			// user.setUserCategory(basic.getOrgCategory());
			user.setUserMobile(address.getOrgMobileNo());
			user.setUserIsActive("Y");
			user.setFirstLoginFlag("Y");
			// user.setUserPassword("**********");
			user.setNoOfAttempt(0);
			user.setIsLock("N");
			user.setCreatedBy(basic.getOrgCreatedBy());
			user.setCreatedOn(now);
			userMstrRepo.save(user);

			// 2. Save tbl_mst_registration
			// RegistrationMstr reg = new RegistrationMstr();
			RegistrationMstr reg = regMstrRepo.findByOrgId(basic.getOrgId())
					.orElseThrow(() -> new RuntimeException("Registration not found for OrgId: " + basic.getOrgId()));

			reg.setOrgId(basic.getOrgId());
			reg.setOrgName(basic.getOrgName());
			reg.setUnifiedLicense(basic.isUnifiedLicense());
			reg.setOrgType(basic.getOrgType());
			reg.setOrgConsttnDate(new Timestamp(basic.getOrgConsttnDate().getTime()));
			reg.setOrgBaseMarket(basic.getOrgBaseMarket());

			reg.setOrgApplicantName(address.getOrgApplicantName());
			reg.setOrgApplicantParentName(address.getOrgApplicantParentName());
			reg.setOrgAddress(address.getOrgAddress());
			reg.setOrgState(address.getOrgState());
			reg.setOrgCity(address.getOrgCity());
			reg.setOrgDist(address.getOrgDist());
			reg.setOrgPoliceStation(address.getOrgPoliceStation());
			reg.setOrgPostOffice(address.getOrgPostOffice());
			reg.setOrgMobileNo(address.getOrgMobileNo());
			reg.setOrgPin(address.getOrgPin());
			reg.setOrgBlockName(address.getOrgBlockName());
			reg.setOrgIsActive("Y");
			reg.setLicenseExists(basic.isHasExistingLicense());
			reg.setHasExistingLicense(basic.isHasExistingLicense());
			reg.setIsRenewed("No");

			if (basic.getOrgCategory() == "RMC" || basic.getOrgCategory() == "BRD") {
				reg.setIsMock("Y");
			} else {
				reg.setIsMock("N");
			}

			reg.setCreatedBy(basic.getOrgCreatedBy());
			reg.setCreatedOn(now);

			// Getting district code from market address - if unified than districtCode =
			// "0"
			// String districtCode = null;
			// if (basic.isUnifiedLicense() == false) {
			// for (RegisterBusinessAddressDTO b : address.getBusinessAddresses()) {
			// districtCode = b.getOrgMarketDist();
			// }
			// } else {
			// districtCode = "0";
			// }
			//
			// Optional<OrgOfficeDetails> officeDetails =
			// officeDetailsRepo.findByDistrictCodeAndOrgCategory(districtCode,
			// basic.getOrgCategory());
			//
			// if (officeDetails.isPresent()) {
			// reg.setOrgOfficeCode(officeDetails.get().getOrgOfficeCode()); // do create
			// the office Id based on
			// // districtCode and orgType
			// } else {
			// reg.setOrgOfficeCode("Default");
			// }
			// Office code generation end here

			regMstrRepo.save(reg);

			// 3. Save tbl_business_address
			for (RegisterBusinessAddressDTO b : address.getBusinessAddresses()) {
				RegisterBusinessAddressDetails businessAddress = new RegisterBusinessAddressDetails();
				RegisterBusinessAddressId id = new RegisterBusinessAddressId();

				id.setOrgId(b.getOrgId());
				id.setOrgBaseMarket(b.getOrgBaseMarket());

				businessAddress.setId(id);
				businessAddress.setOrgMarketAddress(b.getOrgMarketAddress());
				businessAddress.setOrgMarketState(b.getOrgMarketState());
				businessAddress.setOrgMarketCity(b.getOrgMarketCity());
				businessAddress.setOrgMarketDist(b.getOrgMarketDist());
				businessAddress.setOrgMarketPoliceStation(b.getOrgMarketPoliceStation());
				businessAddress.setOrgMarketPostOffice(b.getOrgMarketPostOffice());
				businessAddress.setOrgMarketPin(b.getOrgMarketPin());
				businessAddress.setOrgMarketBlockName(b.getOrgMarketBlockName());
				businessAddress.setOrgCreatedOn(now);
				businessAddress.setOrgCreatedBy(b.getOrgCreatedBy());
				businessRepo.save(businessAddress);

			}

			// 4. Save tbl_license_details
			if (basic.isHasExistingLicense()) { // condition if existing license present then true other wise false
				LicneseMstr lic = new LicneseMstr();
				lic.setOrgId(license.getOrgId());
				lic.setLicenseNo(license.getLicenseNo());
				lic.setLicenseBookNo(license.getLicenseBookNo());
				lic.setLicenseReceiptBookNo(license.getLicenseReceiptBookNo());
				lic.setLicenseReceiptNo(license.getLicenseReceiptNo());
				lic.setOrgValidFrom(license.getLicenseFromDate());
				lic.setRegFeeValidity(license.getLicenseToDate());
				licenseMstrRepo.save(lic);
			}

			// 5. Save tbl_bank_gst
			BankGstMstr bank = new BankGstMstr();
			bank.setOrgId(bankGst.getOrgId());
			bank.setOrgGSTNo(bankGst.getOrgGstNo());
			bank.setOrgGSTState(bankGst.getOrgGstState());
			bank.setOrgBackAccNo(bankGst.getOrgBankAccNo());
			bank.setOrgIFSCCode(bankGst.getOrgIfscCode());
			bank.setCreatedBy(bankGst.getOrgCreatedBy());
			bank.setCreatedOn(bankGst.getOrgCreatedOn());
			bankMstrRepo.save(bank);

			// 6. Save tbl_additional_details
			if (additionalDetails != null) {
				RegisterAdditionalDetails additional = new RegisterAdditionalDetails();
				additional.setOrgId(additionalDetails.getOrgId());
				additional.setIsBGReady(additionalDetails.getIsBGReady());
				additional.setCommodityCode(additionalDetails.getCommodityCode());
				additional.setLandDetails(additionalDetails.getLandDetails());
				additional.setIsRegistered(additionalDetails.getIsRegistered());
				additional.setRegisteredWith(additionalDetails.getRegisteredWith());
				additional.setRegistrationDetails(additionalDetails.getRegistrationDetails());
				additional.setMarketYearApp(additionalDetails.getMarketYearApp());
				additional.setOrgValidFrom(additionalDetails.getOrgValidFrom());
				additional.setRegFeeValidity(additionalDetails.getRegFeeValidity());
				additional.setOrgContactPerson(additionalDetails.getOrgContactPerson());
				additional.setImpExp(additionalDetails.getImpExp());
				additional.setImpExpCommodities(additionalDetails.getImpExpCommodities());
				additional.setAmenitiesDetails(additionalDetails.getAmenitiesDetails());
				additional.setTransactionDetails(additionalDetails.getTransactionDetails());
				additional.setHasPreviousExp(additionalDetails.getHasPreviousExp());
				additional.setPrevExpRemarks(additionalDetails.getPrevExpRemarks());
				additional.setHadLicense(additionalDetails.getHadLicense());
				additional.setPrevLicenseRemarks(additionalDetails.getPrevLicenseRemarks());
				additional.setHadMarketLicense(additionalDetails.getHadMarketLicense());
				additional.setPrevMarketLicenseRemarks(additionalDetails.getPrevMarketLicenseRemarks());
				additional.setIsGuilty(additionalDetails.getIsGuilty());
				additional.setGuiltyRemarks(additionalDetails.getGuiltyRemarks());
				additional.setGodownDetailsRemarks(additionalDetails.getGodownDetailsRemarks());
				additional.setPrivateMarketYardArea(additionalDetails.getPrivateMarketYardArea());
				additional.setStyleForPrivateMarketYard(additionalDetails.getStyleForPrivateMarketYard());
				additional.setSituationPrivateMarketYard(additionalDetails.getSituationPrivateMarketYard());
				additional.setNatureOfInterestOnLand(additionalDetails.getNatureOfInterestOnLand());
				additional.setEmployeeDetails(additionalDetails.getEmployeeDetails());
				additional.setOfficial1(additionalDetails.getOfficial1());
				additional.setOfficial1Father(additionalDetails.getOfficial1Father());
				additional.setOfficial2(additionalDetails.getOfficial2());
				additional.setOfficial2Father(additionalDetails.getOfficial2Father());
				additional.setOfficial3(additionalDetails.getOfficial3());
				additional.setOfficial3Father(additionalDetails.getOfficial3Father());
				additional.setCreatedBy(basic.getOrgCreatedBy());
				additional.setModifiedBy(basic.getOrgCreatedBy());

				additionalDetailsRepo.save(additional);

				if (!reg.isUnifiedLicense()) {
					AccountMstr accMstr = new AccountMstr();
					accMstr.setOfficeCode(reg.getOrgOfficeCode());
					accMstr.setPartyCode(reg.getOrgId());
					accMstr.setVirtualAccCode("WBSAMB" + reg.getOrgId());
					accMstr.setCreatedBy("SELF");
					accMstr.setCreatedOn(LocalDateTime.now());
					accMstrRepo.save(accMstr);
				}

			} else {
				log.error("Something went wrong in additional details");
			}

		} catch (Exception e) {
			log.error("Exception : ", e);
			throw e;
		}
		log.info("Exiting from saveFinalSubmission() :: RegServiceImpl");
	}

	// Final method for saving the registration details through orgId
	@Override
	public void saveFinalSubmission(String token) throws IOException {
		log.info("Inside saveFinalSubmission11() :: RegServiceImpl");
		LocalDateTime now = LocalDateTime.now();

		try {
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 🔒 Validate Token
			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// 🆔 Extract userId from token
			String userId = ts.extractUserId(token); // Example: UEPUMDL00007
			// 📌 Extract original orgId (remove the first letter 'U')
			String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

			log.info("Final submission started for orgId={}", orgId);

			// ================= REGISTRATION FEE CHECK =================
			validateRegistrationFeePaid(orgId);

			RegistrationMstr reg = regMstrRepo.findByOrgId(orgId)
					.orElseThrow(() -> new RuntimeException("Registration not found for OrgId: " + orgId));

			UserMstr user = userMstrRepo.findByOrgId(orgId);
			if (user == null) {
				throw new IllegalArgumentException("User not found for OrgId: " + orgId);
			}

			Optional<RegisterBasicInfoAddress> basic = basicInfoRepo.findByOrgId(orgId);
			if (basic == null) {
				throw new IllegalArgumentException("BasicInfoAddress not found for OrgId: " + orgId);
			}

			List<RegisterBusinessAddress> businessAddresses = businessAddressRepo.findByIdOrgId(orgId);
			BankGstDetails bankGst = bankRepo.findByOrgId(orgId);
			LicenseeDetailsTemp license = licenseRepo.findByOrgId(orgId);
			RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo.findByOrgId(orgId);

			RegistrationStatus regStatus = new RegistrationStatus();
			regStatus.setOrgId(orgId);
			regStatus.setApplicationStatus(OrgConstants.REGISTRATION_STATUS.SAVED);
			regStatus.setActionDateTime(now);
			regStatus.setActionTakenBy("SELF");
			regStatus.setRemarks("SAVED");
			regStatusRepo.save(regStatus);

			user.setUserName(basic.get().getOrgName());
			user.setUserMobile(basic.get().getOrgMobileNo());
			user.setUserIsActive("Y");
			user.setFirstLoginFlag("Y");
			user.setNoOfAttempt(0);
			user.setIsLock("N");
			userMstrRepo.save(user);

			// 4️. Update RegistrationMstr
			reg.setOrgName(basic.get().getOrgName());
			reg.setUnifiedLicense(basic.get().isUnifiedLicense());
			reg.setOrgType(basic.get().getOrgType());
			reg.setOrgConsttnDate(basic.get().getOrgConsttnDate());
			reg.setOrgBaseMarket(basic.get().getOrgBaseMarket());
			reg.setOrgApplicantName(basic.get().getOrgApplicantName());
			reg.setOrgApplicantParentName(basic.get().getOrgApplicantParentName());
			reg.setOrgAddress(basic.get().getOrgAddress());
			reg.setOrgState(basic.get().getOrgState());
			reg.setOrgCity(basic.get().getOrgCity());
			reg.setOrgDist(basic.get().getOrgDist());
			reg.setOrgPoliceStation(basic.get().getOrgPoliceStation());
			reg.setOrgPostOffice(basic.get().getOrgPostOffice());
			reg.setOrgMobileNo(basic.get().getOrgMobileNo());
			reg.setOrgPin(basic.get().getOrgPin());
			reg.setOrgBlockName(basic.get().getOrgBlockName());
			reg.setLicenseExists(basic.get().isHasExistingLicense());
			reg.setHasExistingLicense(basic.get().isHasExistingLicense());
			reg.setIsMock(
					("RMC".equals(basic.get().getOrgCategory()) || "BRD".equals(basic.get().getOrgCategory())) ? "Y"
							: "N");
			reg.setOrgIsActive("Y");
			reg.setIsRenewed("No");
			regMstrRepo.save(reg);

			if (businessAddresses != null && !businessAddresses.isEmpty()) {
				for (RegisterBusinessAddress b : businessAddresses) {
					RegisterBusinessAddressDetails businessAddress = new RegisterBusinessAddressDetails();
					RegisterBusinessAddressId id = new RegisterBusinessAddressId();

					id.setOrgId(basic.get().getOrgId());
					id.setOrgBaseMarket(basic.get().getOrgBaseMarket());
					businessAddress.setId(id);
					businessAddress.setOrgMarketAddress(b.getOrgMarketAddress());
					businessAddress.setOrgMarketState(b.getOrgMarketState());
					businessAddress.setOrgMarketCity(b.getOrgMarketCity());
					businessAddress.setOrgMarketDist(b.getOrgMarketDist());
					businessAddress.setOrgMarketPoliceStation(b.getOrgMarketPoliceStation());
					businessAddress.setOrgMarketPostOffice(b.getOrgMarketPostOffice());
					businessAddress.setOrgMarketPin(b.getOrgMarketPin());
					businessAddress.setOrgMarketBlockName(b.getOrgMarketBlockName());
					businessAddress.setOrgCreatedOn(now);
					businessAddress.setOrgCreatedBy(b.getOrgCreatedBy());
					businessRepo.save(businessAddress);
				}
			}

			if (basic.get().isHasExistingLicense() && license != null) {
				LicneseMstr lic = new LicneseMstr();
				lic.setOrgId(license.getOrgId());
				lic.setLicenseNo(license.getLicenseNo());
				lic.setLicenseBookNo(license.getLicenseBookNo());
				lic.setLicenseReceiptBookNo(license.getLicenseReceiptBookNo());
				lic.setLicenseReceiptNo(license.getLicenseReceiptNo());
				lic.setOrgValidFrom(license.getOrgValidFrom());
				lic.setRegFeeValidity(license.getRegFeeValidity());
				licenseMstrRepo.save(lic);
				System.out.println("lic" + lic);

			}

			if (bankGst != null) {
				BankGstMstr bank = new BankGstMstr();
				bank.setOrgId(bankGst.getOrgId());
				bank.setOrgGSTNo(bankGst.getOrgGstNo());
				bank.setOrgGSTState(bankGst.getOrgGstState());
				bank.setOrgBackAccNo(bankGst.getOrgBankAccNo());
				bank.setOrgIFSCCode(bankGst.getOrgIfscCode());
				bank.setCreatedBy(bankGst.getOrgCreatedBy());
				bank.setCreatedOn(bankGst.getOrgCreatedOn());
				bankMstrRepo.save(bank);

			} else {
				log.warn("No Bank/GST details found for OrgId: {}", orgId);
			}

			AccountMstr accMstr = new AccountMstr();
			accMstr.setOfficeCode(reg.getOrgOfficeCode());
			accMstr.setPartyCode(orgId);
			accMstr.setVirtualAccCode("WBSAMB" + orgId);
			accMstr.setCreatedBy("SELF");
			accMstr.setCreatedOn(now);
			accMstrRepo.save(accMstr);

			if (additionalDetails != null) {
				RegisterAdditionalDetails additional = new RegisterAdditionalDetails();
				additional.setOrgId(additionalDetails.getOrgId());
				additional.setIsBGReady(additionalDetails.getIsBGReady());
				additional.setCommodityCode(additionalDetails.getCommodityCode());
				additional.setLandDetails(additionalDetails.getLandDetails());
				additional.setIsRegistered(additionalDetails.getIsRegistered());
				additional.setRegisteredWith(additionalDetails.getRegisteredWith());
				additional.setRegistrationDetails(additionalDetails.getRegistrationDetails());
				additional.setMarketYearApp(additionalDetails.getMarketYearApp());
				additional.setOrgValidFrom(additionalDetails.getOrgValidFrom());
				additional.setRegFeeValidity(additionalDetails.getRegFeeValidity());
				additional.setOrgContactPerson(additionalDetails.getOrgContactPerson());
				additional.setImpExp(additionalDetails.getImpExp());
				additional.setImpExpCommodities(additionalDetails.getImpExpCommodities());
				additional.setAmenitiesDetails(additionalDetails.getAmenitiesDetails());
				additional.setTransactionDetails(additionalDetails.getTransactionDetails());
				additional.setHasPreviousExp(additionalDetails.getHasPreviousExp());
				additional.setPrevExpRemarks(additionalDetails.getPrevExpRemarks());
				additional.setHadLicense(additionalDetails.getHadLicense());
				additional.setPrevLicenseRemarks(additionalDetails.getPrevLicenseRemarks());
				additional.setHadMarketLicense(additionalDetails.getHadMarketLicense());
				additional.setPrevMarketLicenseRemarks(additionalDetails.getPrevMarketLicenseRemarks());
				additional.setIsGuilty(additionalDetails.getIsGuilty());
				additional.setGuiltyRemarks(additionalDetails.getGuiltyRemarks());
				additional.setGodownDetailsRemarks(additionalDetails.getGodownDetailsRemarks());
				additional.setPrivateMarketYardArea(additionalDetails.getPrivateMarketYardArea());
				additional.setStyleForPrivateMarketYard(additionalDetails.getStyleForPrivateMarketYard());
				additional.setSituationPrivateMarketYard(additionalDetails.getSituationPrivateMarketYard());
				additional.setNatureOfInterestOnLand(additionalDetails.getNatureOfInterestOnLand());
				additional.setEmployeeDetails(additionalDetails.getEmployeeDetails());
				additional.setOfficial1(additionalDetails.getOfficial1());
				additional.setOfficial1Father(additionalDetails.getOfficial1Father());
				additional.setOfficial2(additionalDetails.getOfficial2());
				additional.setOfficial2Father(additionalDetails.getOfficial2Father());
				additional.setOfficial3(additionalDetails.getOfficial3());
				additional.setOfficial3Father(additionalDetails.getOfficial3Father());
				additional.setCreatedBy(basic.get().getOrgCreatedBy());
				additional.setCreatedOn(now);

				additionalDetailsRepo.save(additional);

			} else {
				log.warn("Additional details missing for orgId {}", orgId);
			}

			log.info("Final submission completed successfully for orgId: {}", orgId);

		} catch (Exception e) {
			log.error("Exception in saveFinalSubmission(): ", e);

			throw e;
		}

		log.info("Exiting from saveFinalSubmission() :: RegServiceImpl");
	}

	@Override
	public UserMstr changePassword(ChangePasswordDTO request) {
		log.info("Inside changePassword() :: RegServiceImpl for userId={}", request.getUserId());

		try {
			if (request.getUserId() == null || request.getUserId().isBlank()) {
				throw new IllegalArgumentException("UserId is required");
			}

			if (request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
				throw new IllegalArgumentException("Password fields cannot be null");
			}

			UserMstr user = userMstrRepo.findByUserId(request.getUserId());

			if (user == null) {
				log.warn("User not found for ID: {}", request.getUserId());
				throw new IllegalArgumentException("User not found");
			}

			if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
				log.warn("New password and confirm password do not match for userId={}", request.getUserId());
				throw new IllegalArgumentException("New password and confirm password do not match");
			}

			String encryptedPassword = PasswordService.encrypt(request.getNewPassword());
			user.setUserPassword(encryptedPassword);

			UserMstr updatedUser = userMstrRepo.save(user);

			log.info("Password changed successfully for userId={}", request.getUserId());
			return updatedUser;

		} catch (IllegalArgumentException ex) {
			log.error("Validation error in changePassword(): {}", ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			log.error("Unexpected error in changePassword()", ex);
			throw new RuntimeException("Unable to change password. Please try again later.");
		}
	}

	@Override
	public UserMstr changePasswordOTP(ChangeDefaultPasswordDTO request) {
		log.info("Inside changePassword() :: RegServiceImpl");

		try {
			// Fetch user by ID
			UserMstr user = userMstrRepo.findByUserId(request.getUserId());

			if (user == null) {
				log.warn("User not found for ID: " + request.getUserId());
				return null;
			}
			String otp = OrgUtil.getOtp(OrgConstants.OTP_SIZE);

			String msg_parameters = user.getUserId() + "`" + otp + "`" + OrgUtil.getOTPExpiryTimestamp();
			MessageTracker messageTracker = new MessageTracker();
			messageTracker.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTracker.setMsgType("S");
			messageTracker.setMessage(msg_parameters);
			messageTracker.setMobileNo(user.getUserMobile());
			messageTracker.setStatus("P");
			messageTracker.setRemarks("PENDING");
			messageTracker.setOwner("EPERMIT");
			messageTracker.setCreatedOn(LocalDateTime.now());

			MessageTracker saved = msgTrackerRepo.save(messageTracker);
			log.info("OTP saved with ID: {}", saved.getMsgId());

			return user;

		} catch (Exception e) {
			log.error("Error in changePassword() :: ", e);
			return null;
		}
	}

	@Override
	public boolean validateOTP(OtpValidateDTO request) {
		log.info("Inside verifyOTP() for userId: {}", request.getUserId());

		UserMstr user = userMstrRepo.findByUserId(request.getUserId());

		if (user == null) {
			log.warn("User not found for ID: " + request.getUserId());
			return false;
		}

		Optional<MessageTracker> latestOtpRecord = msgTrackerRepo
				.findTopByMobileNoOrderByCreatedOnDesc(user.getUserMobile());
		if (latestOtpRecord.isPresent()) {
			MessageTracker tracker = latestOtpRecord.get();
			String[] parts = tracker.getMessage().split("`");
			String savedOTP = parts[1];
			String expiryTime = parts[2];

			if (savedOTP.equals(request.getOtp())) {
				// check expired or not
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
				if (LocalDateTime.now().isBefore(LocalDateTime.parse(expiryTime, formatter))) {
					log.info("OTP verified successfully for mobileNo : {}", user.getUserMobile());
					return true;
				} else {
					log.warn("OTP expired for mobileNo : {}", user.getUserMobile());
					return false;
				}
			} else {
				log.warn("Invalid OTP send for the mobileNo : {}", user.getUserMobile());
				return false;
			}
		} else {
			log.warn("No OTP found for the mobileNo : {}", user.getUserMobile());
			return false;
		}
	}

	@Override
	public List<TermsAndConditionsResponseDTO> getTermsByFormType(String formType) {
		log.info("Fetching Terms and Conditions for form type : {} ", formType);
		try {
			List<TermsAndConditions> terms = termsRepo.findByFormType(formType);

			if (terms == null || terms.isEmpty()) {
				log.warn("No Terms & Conditions found for formType: {}", formType);
				return List.of(); // return empty list instead of null
			}

			List<TermsAndConditionsResponseDTO> termsResponseList = terms.stream()
					.map(t -> new TermsAndConditionsResponseDTO(t.getId(), t.getDescription(), t.isMandatory()))
					.collect(Collectors.toList());

			log.info("Successfully fetched {} Terms & Conditions for formType: {}", termsResponseList.size(), formType);
			return termsResponseList;
		} catch (Exception e) {
			log.error("Exception while fetching Terms & Conditions for formType: {}", formType, e);
			throw new RuntimeException("Unable to fetch terms for formType: " + formType, e);
		}
	}

	@Override
	public RegistrationPreviewDTO getRegistrationPreview(
			String token,
			String orgId) {

		log.info("Inside getRegistrationPreviewByOrgId() orgId={}", orgId);

		// 🔐 Remove Bearer header
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		// ✅ Validate Token
		Claims claims = ts.validateToken(token);
		if (claims == null) {
			throw new RuntimeException("Invalid Token");
		}

		// ✅ Extract User ID (audit only)
		String userId = ts.extractUserId(token);
		log.info("User {} previewing org {}", userId, orgId);

		if (orgId == null || orgId.isBlank()) {
			throw new RuntimeException("orgId is required");
		}

		// ✅ Single builder call
		return buildPreview(userId, orgId);
	}

	// ==========================================================
	// ✅ FULL PREVIEW BUILDER — YOUR EXISTING LOGIC
	// ==========================================================

	private RegistrationPreviewDTO buildPreview(String userId, String orgId) {

		RegistrationPreviewDTO preview = new RegistrationPreviewDTO();

		// =========================
		// BASIC INFO
		// =========================

		RegisterBasicInfoAddress basic = basicInfoRepo.findByOrgId(orgId)
				.orElseThrow(() -> new RuntimeException("Basic info not found"));

		BasicInfoDTO basicDTO = new BasicInfoDTO();

		basicDTO.setUserId(userId);
		basicDTO.setOrgId(basic.getOrgId());
		basicDTO.setUnifiedLicense(basic.isUnifiedLicense());
		basicDTO.setHasExistingLicense(basic.isHasExistingLicense());
		basicDTO.setOrgCategory(basic.getOrgCategory());
		basicDTO.setOrgName(basic.getOrgName());
		basicDTO.setOrgType(basic.getOrgType());
		basicDTO.setOrgConsttnDate(basic.getOrgConsttnDate());
		basicDTO.setOrgBaseMarket(basic.getOrgBaseMarket());

		basicDTO.setOrgDocType(basic.getOrgDocType());
		basicDTO.setOrgDocNo(basic.getOrgDocNo());
		basicDTO.setOrgCreatedBy(basic.getOrgCreatedBy());
		basicDTO.setOrgModifiedBy(basic.getOrgModifiedBy());

		preview.setBasicInfo(basicDTO);

		// =========================
		// PERMANENT ADDRESS
		// =========================

		PermanentAddressDTO pAddr = new PermanentAddressDTO();

		pAddr.setOrgId(basic.getOrgId());
		pAddr.setOrgApplicantName(basic.getOrgApplicantName());
		pAddr.setOrgApplicantParentName(basic.getOrgApplicantParentName());
		pAddr.setOrgAddress(basic.getOrgAddress());
		pAddr.setOrgState(basic.getOrgState());
		pAddr.setOrgCity(basic.getOrgCity());
		pAddr.setOrgDist(basic.getOrgDist());
		pAddr.setOrgPoliceStation(basic.getOrgPoliceStation());
		pAddr.setOrgPostOffice(basic.getOrgPostOffice());
		pAddr.setOrgMobileNo(basic.getOrgMobileNo());
		pAddr.setOrgPin(basic.getOrgPin());
		pAddr.setOrgBlockName(basic.getOrgBlockName());
		pAddr.setAddressCreatedBy(basic.getOrgCreatedBy());

		preview.setPermanentAddress(pAddr);

		// =========================
		// BUSINESS ADDRESS
		// =========================

		var businessList = businessAddressRepo.findByIdOrgId(orgId);

		var businessDTO = businessList.stream()
				.map(b -> {
					RegisterBusinessAddressDTO d = new RegisterBusinessAddressDTO();
					d.setOrgId(b.getId().getOrgId());
					d.setOrgBaseMarket(b.getId().getOrgBaseMarket());
					d.setOrgMarketAddress(b.getOrgMarketAddress());
					d.setOrgMarketState(b.getOrgMarketState());
					d.setOrgMarketCity(b.getOrgMarketCity());
					d.setOrgMarketDist(b.getOrgMarketDist());
					d.setOrgMarketPoliceStation(b.getOrgMarketPoliceStation());
					d.setOrgMarketPostOffice(b.getOrgMarketPostOffice());
					d.setOrgMarketPhoneNo(b.getOrgMarketPhoneNo());
					d.setOrgMarketPin(b.getOrgMarketPin());
					d.setOrgMarketBlockName(b.getOrgMarketBlockName());
					d.setOrgCreatedBy(b.getOrgCreatedBy());
					return d;
				}).toList();

		preview.setBusinessAddresses(businessDTO);

		// =========================
		// LICENSE
		// =========================

		preview.setLicenseDetails(
				licenseRepo.findByOrgId(orgId));

		// =========================
		// BANK + GST
		// =========================

		BankGstDetails bank = bankRepo.findByOrgId(orgId);

		if (bank != null) {

			BankGstDTO bankDTO = new BankGstDTO();

			bankDTO.setOrgId(bank.getOrgId());
			bankDTO.setOrgGstNo(bank.getOrgGstNo());
			bankDTO.setOrgGstState(bank.getOrgGstState());
			bankDTO.setOrgBankName(bank.getOrgBankName());
			bankDTO.setOrgBankAccNo(bank.getOrgBankAccNo());
			bankDTO.setOrgIfscCode(bank.getOrgIfscCode());
			bankDTO.setOrgBankBranch(bank.getOrgBankBranch());
			bankDTO.setOrgCreatedOn(bank.getOrgCreatedOn());
			bankDTO.setOrgCreatedBy(bank.getOrgCreatedBy());
			bankDTO.setOrgModifiedOn(bank.getOrgModifiedOn());
			bankDTO.setOrgModifiedBy(bank.getOrgModifiedBy());

			preview.setBankGstDetails(bankDTO);
		}

		// =========================
		// ADDITIONAL DETAILS
		// =========================

		RegisterAdditionalDetailsTemp add = additionalDetailsTempRepo.findByOrgId(orgId);

		if (add != null) {

			AdditionalDetailsDTO addDTO = new AdditionalDetailsDTO();

			addDTO.setIsBGReady(add.getIsBGReady());
			addDTO.setCommodityCode(add.getCommodityCode());
			addDTO.setLandDetails(add.getLandDetails());
			addDTO.setIsRegistered(add.getIsRegistered());
			addDTO.setRegisteredWith(add.getRegisteredWith());
			addDTO.setRegistrationDetails(add.getRegistrationDetails());
			addDTO.setMarketYearApp(add.getMarketYearApp());
			addDTO.setOrgValidFrom(add.getOrgValidFrom());
			addDTO.setRegFeeValidity(add.getRegFeeValidity());
			addDTO.setOrgContactPerson(add.getOrgContactPerson());
			addDTO.setImpExp(add.getImpExp());
			addDTO.setImpExpCommodities(add.getImpExpCommodities());
			addDTO.setAmenitiesDetails(add.getAmenitiesDetails());
			addDTO.setTransactionDetails(add.getTransactionDetails());
			addDTO.setHasPreviousExp(add.getHasPreviousExp());
			addDTO.setPrevExpRemarks(add.getPrevExpRemarks());
			addDTO.setHadLicense(add.getHadLicense());
			addDTO.setPrevLicenseRemarks(add.getPrevLicenseRemarks());
			addDTO.setHadMarketLicense(add.getHadMarketLicense());
			addDTO.setPrevMarketLicenseRemarks(add.getPrevMarketLicenseRemarks());
			addDTO.setIsGuilty(add.getIsGuilty());
			addDTO.setGuiltyRemarks(add.getGuiltyRemarks());
			addDTO.setGodownDetailsRemarks(add.getGodownDetailsRemarks());
			addDTO.setPrivateMarketYardArea(add.getPrivateMarketYardArea());
			addDTO.setStyleForPrivateMarketYard(add.getStyleForPrivateMarketYard());
			addDTO.setSituationPrivateMarketYard(add.getSituationPrivateMarketYard());
			addDTO.setNatureOfInterestOnLand(add.getNatureOfInterestOnLand());
			addDTO.setEmployeeDetails(add.getEmployeeDetails());
			addDTO.setOfficial1(add.getOfficial1());
			addDTO.setOfficial1Father(add.getOfficial1Father());
			addDTO.setOfficial2(add.getOfficial2());
			addDTO.setOfficial2Father(add.getOfficial2Father());
			addDTO.setOfficial3(add.getOfficial3());
			addDTO.setOfficial3Father(add.getOfficial3Father());

			preview.setAdditionalDetails(addDTO);
		}

		// =========================
		// DOCUMENTS
		// =========================

		List<DocumentUploadTemp> docs = documentUploadRepo.findByOrgId(orgId);

		if (docs != null && !docs.isEmpty()) {

			List<DocumentDTO> docList = docs.stream().map(d -> {
				DocumentDTO dto = new DocumentDTO();
				dto.setDocName(d.getDocName());
				dto.setDocType(d.getDocType());
				dto.setUploadPath("/register/api/docs/view/" + d.getId()); // clickable link
				return dto;
			}).toList();

			preview.setDocuments(docList);
		}

		return preview;
	}

	@Override
	public void approveRejectRegistration(String token, ScrutinyRequestDTO request) {

		log.info("Inside approveRejectRegistration()");

		// ---------- TOKEN ----------
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		Claims claims = ts.validateToken(token);
		if (claims == null) {
			throw new RuntimeException("Invalid Token");
		}

		String userId = ts.extractUserId(token);

		// ---------- ORG FROM REQUEST ----------
		String orgId = request.getOrgId();
		if (orgId == null || orgId.isBlank()) {
			throw new RuntimeException("orgId is required");
		}

		String remarks = request.getRemarks();

		// ---------- CURRENT STATUS ----------
		RegistrationStatus lastStatus = regStatusRepo.findTopByOrgIdOrderByActionDateTimeDesc(orgId)
				.orElseThrow(() -> new RuntimeException("No registration found"));

		String currentStatus = lastStatus.getApplicationStatus();

		log.info("OrgId={} currentStatus={}", orgId, currentStatus);

		String nextStatus;

		// ---------- FLOW ----------
		if (REGISTRATION_STATUS.ENROLLED.equals(currentStatus)
				|| REGISTRATION_STATUS.SAVED.equals(currentStatus)
				|| REGISTRATION_STATUS.PENDING.equals(currentStatus)
				|| REGISTRATION_STATUS.SUBMITTED.equals(currentStatus)) {

			nextStatus = REGISTRATION_STATUS.SCRUTINY;
		}

		else if (REGISTRATION_STATUS.SCRUTINY.equals(currentStatus)) {

			if (remarks == null || remarks.trim().isEmpty()) {
				throw new RuntimeException("Remarks required");
			}

			String lowerRemarks = remarks.toLowerCase();

			if (lowerRemarks.contains("reject")
					|| lowerRemarks.contains("invalid")
					|| lowerRemarks.contains("not approve")) {

				nextStatus = REGISTRATION_STATUS.REJECTED; // RJ
			} else {
				nextStatus = REGISTRATION_STATUS.FINAL_APPROVED; // FA
			}
		}

		// 🔒 FINAL LOCK
		else if (REGISTRATION_STATUS.REJECTED.equals(currentStatus)
				|| REGISTRATION_STATUS.FINAL_APPROVED.equals(currentStatus)) {

			throw new RuntimeException("Application already finalized");
		}

		else {
			throw new RuntimeException("Invalid state: " + currentStatus);
		}

		// ---------- SAVE ----------
		RegistrationStatus status = new RegistrationStatus();
		status.setOrgId(orgId);
		status.setApplicationStatus(nextStatus);
		status.setRemarks(remarks);
		status.setActionTakenBy(userId);
		status.setActionDateTime(LocalDateTime.now());

		regStatusRepo.save(status);

		log.info("Registration moved {} -> {}", currentStatus, nextStatus);
	}

	private void validateRegistrationFeePaid(String orgId) {

		// 1️⃣ Check registration master
		RegistrationMstr reg = regMstrRepo.findByOrgId(orgId)
				.orElseThrow(() -> new RuntimeException("Registration not found for OrgId: " + orgId));

		Boolean paid = reg.getIsRegistraionFeePaid();

		// ❌ Fee not paid → block
		if (paid == null || !paid) {
			throw new FeeNotPaidException("Registration fee not paid");
		}

		// 2️⃣ Get latest status for this orgId
		Optional<RegistrationStatus> latestStatusOpt = regStatusRepo.findLatestStatus(orgId);

		if (latestStatusOpt.isPresent()) {
			RegistrationStatus latest = latestStatusOpt.get();

			// ❌ Only block if FINAL APPROVED / FINAL SUBMITTED
			if (OrgConstants.REGISTRATION_STATUS.FINAL_APPROVED
					.equals(latest.getApplicationStatus())) {

				throw new FeeAlreadyPaidException(
						"Registration already final approved for orgId " + orgId);
			}
		}

		// ✅ Otherwise → allow save / final submit
	}

	@Override
	public boolean saveUserThumbPrint(String token, UserDigiSignRequestDto dto) {

		try {

			// 🔐 Strip Bearer
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			// ✅ Extract User ID from token (ONLY SOURCE OF TRUTH)
			String loggedInUser = ts.extractUserId(token);
			log.info("User {} saving digi-sign", loggedInUser);

			// 🔍 Check existing active digi-sign for same user
			boolean exists = dsignRepo.existsByUserIdAndIsActive(loggedInUser, "T");
			if (exists) {
				return false; // controller -> Exists
			}

			// 💾 Save
			UserDigiSignEntity entity = new UserDigiSignEntity();
			entity.setUserId(loggedInUser); // 👈 token user
			entity.setThumbPrint(dto.getThumbPrint());
			entity.setAlias(dto.getAlias());
			entity.setIssuer(dto.getIssuer());
			entity.setIsActive("T");
			entity.setCreatedBy(loggedInUser);
			entity.setCreatedOn(LocalDateTime.now());

			dsignRepo.save(entity);
			return true; // controller -> Success

		} catch (Exception e) {
			log.error("Error saving digi-sign", e);
			throw new RuntimeException("DIGI_SIGN_SAVE_FAILED");
		}
	}

	@Override
	public Optional<String> getUserThumbPrint(String token) {

		try {

			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			Claims claims = ts.validateToken(token);
			if (claims == null) {
				throw new RuntimeException("Invalid Token");
			}

			String userId = ts.extractUserId(token);
			log.info("Fetching digi-sign thumbprint for user {}", userId);

			return dsignRepo
					.findByUserIdAndIsActive(userId, "T")
					.map(UserDigiSignEntity::getThumbPrint);

		} catch (Exception e) {
			log.error("Error fetching thumbprint", e);
			return Optional.empty();
		}
	}

}
