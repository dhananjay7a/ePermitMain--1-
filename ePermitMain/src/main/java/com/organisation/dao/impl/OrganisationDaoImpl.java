package com.organisation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.organisation.constants.OrgConstants;
import com.organisation.dao.OrganisationDao;
import com.organisation.model.DropDownMaster;
import com.organisation.model.FormFiveModel;
import com.organisation.model.MarketMaster;
import com.organisation.model.MemberMarketMap;
import com.organisation.model.MessageTracker;
import com.organisation.model.RegAdditionalInfo;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.UserMapping;
import com.organisation.model.UserMaster;
import com.organisation.model.UserSessionBean;

import com.organisation.util.OrgUtil;
import com.organisation.util.RepositoryThreadLocal;

@Repository
public class OrganisationDaoImpl implements OrganisationDao {

	private static final Logger log = LoggerFactory.getLogger(OrganisationDaoImpl.class);
	@Autowired
    private DataSource dataSource;  
	@Override
	public List<DropDownMaster> getOrgTypeForSignUp() {
		log.info("Inside getOrgTypeForSignup():: OrganisationDaoImpl");
		List<DropDownMaster> dropDownList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT org_category, org_category_name, org_category_scope " + "FROM un_org_category_mstr "
					+ "WHERE org_category NOT IN (?, ?, ?, ?) " ;

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, OrgConstants.ORG_CATEGORY.ORG_CATEGORY_HOST);
			pstmt.setString(2, OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RMC);
			pstmt.setString(3, OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BOARD);
			pstmt.setString(4, OrgConstants.ORG_CATEGORY.ORG_CATEGORY_RENTER);


			rs = pstmt.executeQuery();
			while (rs.next()) {
				DropDownMaster dropDown = new DropDownMaster();
				dropDown.setRoleId(rs.getString("org_category"));
				dropDown.setValue(rs.getString("org_category_name"));
				dropDown.setOrgCategoryScope(rs.getString("org_category_scope").trim());
				dropDownList.add(dropDown);
			}

		} catch (Exception e) {
			log.error("Exception : ", e.getMessage(), e);
		} finally {
			
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				log.error("Exception : ", e.getMessage(), e);
			}
		}
		log.info("Exiting from getOrgTypeForSignUp() :: OrgDaoImpl");
		return dropDownList;
	}

	@Override
	public List<MarketMaster> fetchAllMarketsPublic(MarketMaster marketObject) {
		log.info("Inside fetchAllMarketsPublic() :: OrgDaoImpl");
		List<MarketMaster> marketList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		
		try {
	        conn = dataSource.getConnection();
	        query.append("SELECT * FROM un_market_mstr WHERE 1=1 AND is_mock_market = ? ");

	        if (marketObject.isRestrictDenotifiedMkt()) {
	            query.append(" AND is_denotified_market = 'N' ");
	        }

	        query.append(" ORDER BY description DESC ");

	        pstmt = conn.prepareStatement(query.toString());

	        // Manually set parameters
	        int paramIndex = 1;
	        pstmt.setString(paramIndex++, OrgConstants.NO); // is_mock_market = 'N'

	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            MarketMaster beans = new MarketMaster();
	            beans.setMarketCode(rs.getString("MARKET_CODE"));
	            beans.setAccountName(rs.getString("ACCOUNT_NAME"));
	            beans.setAddress(rs.getString("ADDRESS"));
	            beans.setAuthorizedPerson(rs.getString("AUTHORIZED_PERSON"));
	            beans.setBankAccNo(rs.getString("BANK_ACC_NO"));
	            beans.setBankName(rs.getString("BANK_NAME"));
	            beans.setBranchCode(rs.getString("BRANCH_CODE"));
	            beans.setCity(rs.getString("CITY"));
	            beans.setCreatedBy(rs.getString("CREATED_BY"));
	            beans.setCreatedOn(rs.getTimestamp("CREATED_ON"));
	            beans.setDefaultCommodity(rs.getString("DEFAULT_COMMODITY"));
	            beans.setDefaultPurpose(rs.getString("DEFAULT_PURPOSE"));
	            beans.setDescription(rs.getString("DESCRIPTION"));
	            beans.setDistrictCode(rs.getString("DISTRICT_CODE"));
	            beans.setEmailId(rs.getString("EMAIL_ID"));
	            beans.setIfscCode(rs.getString("IFSC_CODE"));
	            beans.setIsDocOnline(rs.getString("IS_DOC_ONLINE"));
	            beans.setIsMockMarket(rs.getString("IS_MOCK_MARKET"));
	            beans.setIssendSmsReqd(rs.getString("IS_SEND_SMS_REQD"));
	            beans.setMarketLanguage(rs.getString("MARKET_LANGUAGE"));
	            beans.setMarketPassword(rs.getString("MARKET_PASSWORD"));
	            beans.setMobileNo(rs.getString("MOBILE_NO"));
	            beans.setModified_by(rs.getString("MODIFIED_BY"));
	            beans.setModifiedOn(rs.getTimestamp("MODIFIED_ON"));
	            beans.setPincode(rs.getString("PINCODE"));
	            beans.setPurchaseBillFormat(rs.getString("PURCHASE_BILL_FORMAT"));
	            beans.setRegistrationNo(rs.getString("REGISTRATION_NO"));
	            beans.setRoundOff(rs.getBigDecimal("ROUND_OFF"));
	            beans.setSettVoucherFormat(rs.getString("SETT_VOUCHER_FORMAT"));
	            beans.setShortName(rs.getString("SHORT_NAME"));
	            beans.setSmsMobileNo(rs.getString("SMS_MOBILE_NO"));
	            beans.setStateCode(rs.getString("STATE_CODE"));
	            beans.setTelePhoneNo(rs.getString("TELE_PHONE_NO"));
	            beans.setBranchName(rs.getString("BRANCH_NAME"));
	            marketList.add(beans);
	        }

	    } catch (Exception e) {
	        log.error("Exception " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ex) {
	            log.error("Exception in closing resources: " + ex.getMessage(), ex);
	        }
	    }
	    log.info("Exit fetchAllMarketsPublic() :: OrgDaoImpl");
	    return marketList;
	}

	// This otp will generate when user want to self enrollment
	@Override
	public boolean saveOTP(MessageTracker messageTracker) {

		log.info("Inside saveOTP():: OrgDaoImpl");

		boolean isInserted = false;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			String sql = "INSERT INTO MESSAGE_TRACKER (MSG_TRANSCODE, MSG_MESSAGE_TYPE, MSG_PARAMETERS, MSG_MOBILE_NOS, MSG_STATUS, MSG_REMARKS, MSG_CREATED_ON, MSG_OWNER) "
					+ "VALUES (?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP, ?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, messageTracker.getTranscode());
			pstmt.setString(2, messageTracker.getMsgType());
			pstmt.setString(3, messageTracker.getMessage());
			pstmt.setString(4, messageTracker.getMobileNo());
			pstmt.setString(5, messageTracker.getStatus());
			pstmt.setString(6, messageTracker.getRemarks());
			pstmt.setString(7, messageTracker.getOwner());

			int rowInserted = pstmt.executeUpdate();

			if (rowInserted > 0) {
				conn.commit();
				isInserted = true;
				log.info("Successfully saved SMS data for MobileNo: " + messageTracker.getMobileNo());
			}
		} catch (Exception e) {
			log.error("Exception while inserting into MESSAGE_TRACKER: ", e);
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception rollbackEx) {
				log.error("Rollback failed: ", rollbackEx);
			}
		} finally {
			
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception ex) {
				log.error("Error closing PreparedStatement: ", ex);
			}
		}
		log.info("Exiting from saveOTP():: OrgDaoImpl");
		return isInserted;
	}

	
	
	
	@Override
	public boolean checkOrgCatIsHost(RegistrationMaster registrationObj, final Connection conn) {
		log.info("Inside checkOrgCatIsHost() :: OrgDaoImpl");
		boolean isHost = false;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct IS_HOST from un_role_mstr where org_category = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, registrationObj.getOrgCategory());

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String categoryIsHost = rs.getString("is_host");
				if (OrgUtil.isNeitherNullNorEmpty(categoryIsHost) && categoryIsHost.equals(OrgConstants.YES)) {
					isHost = true;
				}
			}

		} catch (Exception e) {
			log.error("Error in checkOrgCatIsHost() : " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
				log.error("Error closing resources in checkOrgCatIsHost(): " + e.getMessage());
			}
		}
		log.info("Exiting from checkOrgCatIsHost() :: OrgDaoImpl");
		return isHost;
	}

	// this function is basically used for getting flag after checking is auto approval
	@Override
	public String getIsAutoApprovalflag(String orgCategory, Connection conn) {
		String response = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT is_auto_approve FROM un_org_category_mstr WHERE org_category = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, orgCategory);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				response = rs.getString("is_auto_approve");
			}

		} catch (Exception e) {
			log.error("Exception in getIsAutoApprovalFlag() : " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
				log.error("Error while closing DB resources in getIsAutoApprovalflag(): " + e.getMessage(), e);
			}
		}

		return response;
	}

	// This function used for finding the scope of organization
	public RegistrationMaster fetchScopeOfOrg(RegistrationMaster registrationMaster, final Connection conn)
			throws Exception {
		log.info("Inside fetchScopeOfOrg() :: OrgDaoImpl");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		RegistrationMaster result = null;

		String sql = "SELECT org_category_scope, org_user_scope, ORG_DFLT_USER_ROLE FROM un_ORG_CATEGORY_MSTR WHERE org_category = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, registrationMaster.getOrgCategory());

			rs = pstmt.executeQuery();

			if (rs != null && rs.next()) {
				result = new RegistrationMaster();
				result.setScopeOrg(rs.getString("org_category_scope").trim());
				result.setScopeUsr(rs.getString("org_user_scope").trim());
				result.setUserDfltRole(rs.getString("ORG_DFLT_USER_ROLE"));

			}
		} catch (Exception e) {    
			log.error("Exception : ", e);
		}

		return result;
	}

	// insert RegistrationMaster data into the database table
	@Override
	public int createOrgReg(RegistrationMaster registrationObj, Connection conn) throws Exception {

		if (conn == null || conn.isClosed()) {
			log.error("Exception :: connection closed so reopen ");
			conn = dataSource.getConnection();
		}

		log.info("Inside createOrgReg() :: OrgDaoImpl -- OrgId - " + registrationObj.getOrgId());
		int insertDestination = 0;
		PreparedStatement pstmt = null;

		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO un_registration_mstr(");
		sql.append("org_id, org_name, org_category, org_type, org_cnstttn_date, org_address, org_city, ");
		sql.append("org_state_code, org_district_code, org_pin, org_phoneno, org_contact_person, ");
		sql.append("org_contact_mobile, org_contact_email, org_perm_add, org_perm_city, org_perm_state, ");
		sql.append("org_perm_dist, org_perm_pin, org_panno, org_adharno, org_gstno, org_gst_state, ");
		sql.append("org_bank_acc_no, org_bank_name, org_bank_branch, org_ifsc_code, org_first_login, ");
		sql.append("org_password, org_enc_password, org_is_active, org_is_mock, org_request_no, ");
		sql.append("org_request_status, org_otp_generation_time, org_otp_expiry_time, org_is_otp, ");
		sql.append("org_first_login_flag, org_is_neg_invt_allowed, org_vir_acc_no, org_category_scope, ");
		sql.append("org_created_by, org_created_on, is_registration_fee_paid, org_doc_type, org_doc_no, ");
		sql.append("org_base_market, org_police_station, org_post_office, license_no, license_book_no, ");
		sql.append("license_receipt_no, license_book_receipt_no, org_valid_from, registration_fee_validity, ");
		sql.append("is_license_exist, org_renewal_status, applicant_name, IS_UNIFIED, is_auto_approve, ");
		sql.append("org_block_name, org_perm_block_name) ");
		sql.append(
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sql.append("?, ?, ?, ?, ?, ?)");

		try {
			pstmt = conn.prepareStatement(sql.toString());
			int index = 1;

			pstmt.setString(index++, registrationObj.getOrgId());
			pstmt.setString(index++, registrationObj.getOrgName());
			pstmt.setString(index++, registrationObj.getOrgCategory());
			pstmt.setString(index++, registrationObj.getOrgType());
			pstmt.setTimestamp(index++, registrationObj.getOrgConsttnDate());
			pstmt.setString(index++, registrationObj.getOrgAddress());
			pstmt.setString(index++, registrationObj.getOrgCity());
			pstmt.setString(index++, registrationObj.getOrgState());
			pstmt.setString(index++, registrationObj.getOrgDistrict());
			pstmt.setString(index++, registrationObj.getOrgPinCode());
			pstmt.setString(index++, registrationObj.getOrgMobileNo());
			pstmt.setString(index++, registrationObj.getOrgContactPerson());
			pstmt.setString(index++, registrationObj.getOrgContactMobile());
			pstmt.setString(index++, registrationObj.getOrgContactEmail());
			pstmt.setString(index++, registrationObj.getOrgPermAddr());
			pstmt.setString(index++, registrationObj.getOrgPermCity());
			pstmt.setString(index++, registrationObj.getOrgPermState());
			pstmt.setString(index++, registrationObj.getOrgPermDist());
			pstmt.setString(index++, registrationObj.getOrgPermPin());
			pstmt.setString(index++, registrationObj.getOrgPan());
			pstmt.setString(index++, registrationObj.getOrgAdharNo());
			pstmt.setString(index++, registrationObj.getOrgGSTNo());
			pstmt.setString(index++, registrationObj.getOrgGSTState());
			pstmt.setString(index++, registrationObj.getOrgBackAccNo());
			pstmt.setString(index++, registrationObj.getOrgBankName());
			pstmt.setString(index++, registrationObj.getOrgBankBranch());
			pstmt.setString(index++, registrationObj.getOrgIFSCCode());
			pstmt.setString(index++, OrgConstants.FIRST_LOGIN_FLAG);
			pstmt.setString(index++, registrationObj.getPassword());
			pstmt.setString(index++, registrationObj.getEncryptedPassword());
			pstmt.setString(index++, OrgConstants.IS_ACTIVE);
			pstmt.setString(index++, OrgConstants.FALSE);
			pstmt.setString(index++, registrationObj.getOrgRequestNo());
			pstmt.setString(index++, registrationObj.getOrgRequestStatus());
			pstmt.setTimestamp(index++, registrationObj.getOtpGenTime());
			pstmt.setTimestamp(index++, registrationObj.getOtpExpiryTime());
			pstmt.setString(index++, registrationObj.getIsOTP());
			pstmt.setString(index++, OrgConstants.FIRST_LOGIN_FLAG);
			pstmt.setString(index++, OrgConstants.FALSE);
			pstmt.setString(index++, registrationObj.getVirAccCode());
			pstmt.setString(index++, registrationObj.getOrgCategoryScope());
			pstmt.setString(index++, registrationObj.getOrgCreatedBy());
			pstmt.setString(index++, registrationObj.isLicenseExists() ? OrgConstants.TRUE : OrgConstants.FALSE);
			pstmt.setString(index++, registrationObj.getOrgDocType());
			pstmt.setString(index++, registrationObj.getOrgDocNo());
			pstmt.setString(index++, registrationObj.isUnifiedLicense() ? "ALL" : registrationObj.getOrgBaseMarket());
			pstmt.setString(index++, registrationObj.getOrgPoliceStation());
			pstmt.setString(index++, registrationObj.getOrgPostOffice());
			pstmt.setString(index++, registrationObj.getLicenseno());
			pstmt.setString(index++, registrationObj.getLicenseBookNo());
			pstmt.setString(index++, registrationObj.getLicenseReceiptNo());
			pstmt.setString(index++, registrationObj.getLicenseBookReceiptNo());
			pstmt.setTimestamp(index++, registrationObj.getOrgValidFrom());
			pstmt.setTimestamp(index++, registrationObj.getRegFeeValidity());
			pstmt.setBoolean(index++, registrationObj.isLicenseExists());
			pstmt.setString(index++, registrationObj.getOrgRenewalStatus());
			pstmt.setString(index++, registrationObj.getApplicantName());
			pstmt.setBoolean(index++, registrationObj.isUnifiedLicense());
			pstmt.setString(index++, registrationObj.getIsAutoApprove());
			pstmt.setString(index++, registrationObj.getOrgBlockName());
			pstmt.setString(index++, registrationObj.getOrgPermBlockName());

			insertDestination = pstmt.executeUpdate();
			if (insertDestination != 1) {
				log.error("Insert into un_registration_mstr failed for orgId : " + registrationObj.getOrgId());
			}

		} catch (Exception e) {
			log.error("Exception - " + e.getMessage());
		}

		return insertDestination;
	}

	// Insert data into un_user_mstr table into the database
	@Override
	public int createDefaultuser(RegistrationMaster registrationObj, Connection conn) throws Exception {
		log.info("Inside createDefaultUser() :: orgId - " + registrationObj.getOrgId());
		int insertDestination = 1;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO UN_USER_MSTR (USER_ID, ORG_ID, USER_NAME, USER_CATEGORY, USER_MOBILE, ");
		sql.append("USER_EMAIL, USER_FIRST_LOGIN, USER_PASSWORD, USER_ENC_PASSWORD, USER_IS_ACTIVE, USER_MAKER, ");
		sql.append("USER_CHECKER, CREATED_BY, CREATED_ON, ORG_USER_SCOPE, USER_ADDRESS, USER_OTP, USER_OTP_EXPIRY) ");
		sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, registrationObj.getUserId().toUpperCase());
			pstmt.setString(2, registrationObj.getOrgId());
			pstmt.setString(3, registrationObj.getOrgName());
			pstmt.setString(4, registrationObj.getOrgCategory());
			pstmt.setString(5, "91" + registrationObj.getOrgContactMobile());

			pstmt.setString(6, registrationObj.getEmailId());
			pstmt.setString(7, OrgConstants.FIRST_LOGIN_FLAG);
			pstmt.setString(8, registrationObj.getPassword());
			pstmt.setString(9, registrationObj.getEncryptedPassword());
			pstmt.setString(10, OrgConstants.TRUE);
			pstmt.setString(11, OrgConstants.IS_ACTIVE);

			pstmt.setString(12, OrgConstants.IS_ACTIVE);
			pstmt.setString(13, registrationObj.getOrgId());
			pstmt.setTimestamp(14, OrgUtil.getCurrentTimestamp());
			pstmt.setString(15, registrationObj.getScopeUsr());
			pstmt.setString(16, registrationObj.getOrgAddress());
			pstmt.setString(17, registrationObj.getPassword());
			pstmt.setTimestamp(18, registrationObj.getOtpExpiryTime());

			insertDestination = pstmt.executeUpdate();

			if (insertDestination != 1) {
				log.error("insert into un_user_mstr failed ::  orgId - " + registrationObj.getOrgId());
			}

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null)
					conn.commit();

			} catch (Exception e) {
				log.error("Exception : ", e);
			}

		}

		log.info("Exiting createDefaultUser() RegistrationDaoImpl UserId : " + registrationObj.getUserId().toUpperCase()
				+ " is data Inserted : " + insertDestination);

		return insertDestination;
	}

	// This function insert the additional information
	@Override
	public int insertRegAddtnInfo(RegAdditionalInfo regAdditionalInfo, Connection conn) throws Exception {
		log.info("Inside insertRegAddtnInfo() Dao Impl ::" + regAdditionalInfo.getOrgId());
		int result = 0;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO un_reg_additional_info(");
		sql.append("org_id, middle_name, is_registered, registered_with, registration_details, ");
		sql.append("employee_details, market_year_applied, importer_exporter, ");
		sql.append("official1, official1father, official2, official2father, official3, official3father, ");
		sql.append("imp_exp_commodity, is_bg_ready, is_guilty, commodity_code, created_by, created_dt, ");
		sql.append("has_previous_exp, had_license, had_market_license, ");
		sql.append(
				"prev_exp_remarks, prev_license_remarks, prev_market_license_remarks, guilty_remarks, godown_details_remarks, ");
		sql.append("land_details, amenities_details, transaction_details, ");
		sql.append(
				"license_terms, breach_terms, style_pmyard, situation_pmyard, nature_of_interest, private_market_yard_area) ");
		sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		try {
			pstmt = conn.prepareStatement(sql.toString());

			int index = 1;
			pstmt.setString(index++, regAdditionalInfo.getOrgId());
			pstmt.setString(index++, regAdditionalInfo.getMiddleName());
			pstmt.setString(index++, regAdditionalInfo.getIsRegistered());
			pstmt.setString(index++, regAdditionalInfo.getRegisteredWith());
			pstmt.setString(index++, regAdditionalInfo.getRegistrationDetails());

			pstmt.setString(index++, regAdditionalInfo.getEmployeeDetails());
			pstmt.setString(index++, regAdditionalInfo.getMarketYearApp());
			pstmt.setString(index++, regAdditionalInfo.getImpExp());
			pstmt.setString(index++, regAdditionalInfo.getOfficial1());
			pstmt.setString(index++, regAdditionalInfo.getOfficial1Father());

			pstmt.setString(index++, regAdditionalInfo.getOfficial2());
			pstmt.setString(index++, regAdditionalInfo.getOfficial2Father());
			pstmt.setString(index++, regAdditionalInfo.getOfficial3());
			pstmt.setString(index++, regAdditionalInfo.getOfficial3Father());
			pstmt.setString(index++, regAdditionalInfo.getImpExpCommodities());

			pstmt.setString(index++, regAdditionalInfo.getIsBGReady());
			pstmt.setString(index++, regAdditionalInfo.getIsGuilty());
			pstmt.setString(index++, regAdditionalInfo.getCommodityCode());
			pstmt.setString(index++, regAdditionalInfo.getCreatedBy());

			pstmt.setString(index++, regAdditionalInfo.getHasPreviousExp());
			pstmt.setString(index++, regAdditionalInfo.getHadLicense());
			pstmt.setString(index++, regAdditionalInfo.getHadMarketLicense());

			pstmt.setString(index++, regAdditionalInfo.getPrevExpRemarks());
			pstmt.setString(index++, regAdditionalInfo.getPrevLicenseRemarks());
			pstmt.setString(index++, regAdditionalInfo.getPrevMarketLicenseRemarks());
			pstmt.setString(index++, regAdditionalInfo.getGuiltyRemarks());
			pstmt.setString(index++, regAdditionalInfo.getGodownDetailsRemarks());

			pstmt.setString(index++, regAdditionalInfo.getLandDetails());
			pstmt.setString(index++, regAdditionalInfo.getAmenitiesDetails());
			pstmt.setString(index++, regAdditionalInfo.getTransactionDetails());

			pstmt.setString(index++, regAdditionalInfo.getLicenseTerms());
			pstmt.setString(index++, regAdditionalInfo.getBreachTerms());
			pstmt.setString(index++, regAdditionalInfo.getStyleForPrivateMarketYard());
			pstmt.setString(index++, regAdditionalInfo.getSituationPrivateMarketYard());

			pstmt.setString(index++, regAdditionalInfo.getNatureOfInterestOnLand());
			pstmt.setString(index++, regAdditionalInfo.getPrivateMarketYardArea());

			result = pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			} else {
				log.error("Error in closing the PreparedStatement");
			}

		}
		log.info("Exiting fom insertRegAddtnInfo()");
		return result;

	}

	// This function insert data of Form5
	public int insertUpdateForm5Details(FormFiveModel formFiveModel, Connection conn) {
		log.info("Inside insertUpdateForm5Details() :: OrgId - " + formFiveModel.getOrgId());
		int result = 0;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		UserSessionBean user = (UserSessionBean) RepositoryThreadLocal.getValue(OrgConstants.USER_ID);

		try {
			sql.append("INSERT INTO un_form5_details ");
			sql.append(
					"(org_id, seq_no, storage_name, village_name, jl_no, premises_no, frequency, boundary, remarks, created_by, created_on) ");
			sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp) ");
			sql.append("ON CONFLICT (org_id, seq_no) DO UPDATE SET ");
			sql.append("storage_name = ?, village_name = ?, jl_no = ?, premises_no = ?, ");
			sql.append("frequency = ?, boundary = ?, remarks = ?, modified_by = ?, modified_on = current_timestamp");

			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, formFiveModel.getOrgId());
			pstmt.setInt(2, formFiveModel.getSeqNo());
			pstmt.setString(3, formFiveModel.getStorageName());
			pstmt.setString(4, formFiveModel.getVillageName());
			pstmt.setString(5, formFiveModel.getJlNo());
			pstmt.setString(6, formFiveModel.getPremisesNo());
			pstmt.setString(7, formFiveModel.getFrequency());
			pstmt.setString(8, formFiveModel.getBoundary());
			pstmt.setString(9, formFiveModel.getRemarks());
			pstmt.setString(10, user.getUserId()); // created_by

			// --- Parameters for UPDATE part (11 to 18)
			pstmt.setString(11, formFiveModel.getStorageName());
			pstmt.setString(12, formFiveModel.getVillageName());
			pstmt.setString(13, formFiveModel.getJlNo());
			pstmt.setString(14, formFiveModel.getPremisesNo());
			pstmt.setString(15, formFiveModel.getFrequency());
			pstmt.setString(16, formFiveModel.getBoundary());
			pstmt.setString(17, formFiveModel.getRemarks());
			pstmt.setString(18, user.getUserId()); // modified_by

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			log.error("Error : " + e.getMessage());
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				log.error("closing PreparedStatement failed");
			}
		}
		log.info("Exiting from insertUpdateForm5Details()");
		return result;
	}

	// Return all the market codes from the database
	@Override
	public ArrayList<String> fetchAllMarkets() {
		log.info("Inside fetchAllMarkets()");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String> marketCodes = new ArrayList<>();

		try {
			conn = dataSource.getConnection();
			String query = "SELECT market_code FROM un_market_mstr";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				marketCodes.add(rs.getString("market_code"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} finally {
			
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}

		return marketCodes;

	}

	@Override
	public MemberMarketMap saveMemberMarketMap(MemberMarketMap mapBean, final Connection conn) {
		log.info("Inside saveMemberMarketMap()");

		PreparedStatement pstmt = null;
		StringBuilder query = new StringBuilder();
		try {
			query.append(
					" insert into un_member_market_mapping (org_id,market_code,is_active,created_by,created_on,is_registration_fee_paid) values (?,?,?,?,current_timestamp,?) ");
			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, mapBean.getTraderOrgId());
			pstmt.setString(2, mapBean.getMarket());
			pstmt.setString(3, OrgConstants.TRUE);
			pstmt.setString(4, mapBean.getCreatedBy());
			pstmt.setString(5, OrgConstants.FALSE);

			int i = pstmt.executeUpdate();
			if (i > 0) {
				mapBean.setReturnMsg(OrgConstants.SUCCESS_STRING);
			} else {
				mapBean.setReturnMsg(OrgConstants.FAILURE_STRING);
			}

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				log.info("Exiting from saveMemberMarketMap()");
			} catch (Exception ex) {
				log.error("Exception : " + ex.getMessage());
			}
		}

		return mapBean;
	}

	@Override
	public UserMapping saveUserMarketMapping(UserMapping userMarketMap, final Connection conn) {
		log.info("Inside saveUserMarketMapping()");
		PreparedStatement pstmt = null;
		StringBuilder query = new StringBuilder();
		try {

			query.append(
					" insert into un_user_market_mapping (user_id,market_code,is_active,created_by,created_on) values (?,?,?,?,current_timestamp) ");
			log.info("Query to be fired on the database : " + query.toString());
			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, userMarketMap.getUserId());
			pstmt.setString(2, userMarketMap.getMarketCode());
			pstmt.setString(3, OrgConstants.TRUE);
			pstmt.setString(4, userMarketMap.getCreatedBy());
			int i = pstmt.executeUpdate();
			if (i > 0) {
				userMarketMap.setReturnMsg(OrgConstants.SUCCESS_STRING);
			} else {
				userMarketMap.setReturnMsg(OrgConstants.FAILURE_STRING);
			}

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception ex) {
				log.error("Error in closing PreparedStatement: " + ex.getMessage());
			}
		}
		log.info("Exiting from saveUserMarketMapping()");
		return userMarketMap;
	}

	@Override
	public boolean createDefaultTraderRole(RegistrationMaster registrationObj, final Connection conn) {
		log.info("Inside createDefaultTraderRole()");
		PreparedStatement pstmt = null;
		StringBuilder query = new StringBuilder();
		try {
			query.append(" INSERT INTO un_user_role_mapping");
			query.append(" (user_id, role_id, is_active, created_by, created_on) ");
			query.append(" VALUES (?, ?, ?, ?, ?) ");

			registrationObj.setUserId("U" + registrationObj.getOrgId());
			registrationObj.setReturnMsg("");

			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, registrationObj.getUserId().toUpperCase());
			pstmt.setString(2, OrgConstants.TRADER_ROLES.DEFAULT_ROLE);
			pstmt.setString(3, OrgConstants.IS_ACTIVE);
			pstmt.setString(4, registrationObj.getOrgId());
			pstmt.setTimestamp(5, OrgUtil.getCurrentTimestamp());

			int defaultRoleInserted = pstmt.executeUpdate();
			pstmt.close();
			if (defaultRoleInserted > 0) {
				pstmt = conn.prepareStatement(query.toString());
				pstmt.setString(1, registrationObj.getUserId().toUpperCase());
				pstmt.setString(2, OrgConstants.TRADER_ROLES.RENEWAL_ROLE);
				pstmt.setString(3, OrgConstants.FALSE);
				pstmt.setString(4, registrationObj.getOrgId());
				pstmt.setTimestamp(5, OrgUtil.getCurrentTimestamp());

				int renewableRoleInserted = pstmt.executeUpdate();
				return renewableRoleInserted > 0;

			} else {
				log.error("Default role inserted failed :: OrgId - " + registrationObj.getOrgId());
				return false;
			}

		} catch (Exception e) {
			log.error("Exception in createDefaultTraderRole()");
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					log.error("Exception while closing pstmt:: " + e.getMessage());
				}
			}
		}
		log.info("Exiting from createDefaultTraderRole() :: OrganisationDaoImpl");
		return false;
	}

	@Override
	public boolean checkIfAccMstrExists(RegistrationMaster registrationObj, final Connection conn) throws Exception {
		log.info("Inside checkAccMstrExists() :: OrgId - " + registrationObj.getOrgId());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			String sql = "SELECT COUNT(*) FROM un_account_master WHERE account_code = ? AND market_code = ?";
			log.info("Executing SQL : " + sql);

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, registrationObj.getOrgId());
			pstmt.setString(2, registrationObj.getOrgBaseMarket());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				result = count > 0;
			}

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage(), e);
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			log.info("Exiting checkIfAccMstrExists");
		}
		return result;

	}

	@Override
	public int insertAccountMaster(RegistrationMaster registrationObj, final Connection conn) throws Exception {
		log.info("Inside insertAccountMaster() :: OrgId - " + registrationObj.getOrgId());
		int insertResult = 0;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.append("INSERT INTO un_account_master (");
			sql.append("market_code, account_code, acc_type, virtual_acc_code, bank_name, bank_acc_no, ifsc_code, ");
			sql.append("branch_name, account_name, party_code, email_id, address, branch_code, payout_type, ");
			sql.append("isactive, member_type, created_on, created_by, license_no) ");
			sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)");

			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, registrationObj.getOrgBaseMarket());
			pstmt.setString(2, registrationObj.getOrgId());
			pstmt.setString(3, registrationObj.getAccType());
			pstmt.setString(4, registrationObj.getOrgVirAccNo());
			pstmt.setString(5, registrationObj.getOrgBankName());
			pstmt.setString(6, registrationObj.getOrgBankCode());
			pstmt.setString(7, registrationObj.getOrgIFSCCode());
			pstmt.setString(8, registrationObj.getOrgBankBranch());
			pstmt.setString(9, registrationObj.getOrgName());
			pstmt.setString(10, registrationObj.getOrgId());
			pstmt.setString(11, registrationObj.getOrgContactEmail());
			pstmt.setString(12, registrationObj.getOrgAddress());
			pstmt.setString(13, registrationObj.getOrgBankBranch());
			pstmt.setString(14, OrgConstants.PAYOUT_TYPE);
			pstmt.setString(15, OrgConstants.IS_ACTIVE);
			pstmt.setString(16, OrgConstants.ACCOUNT_TYPE.MEMBER);
			pstmt.setString(17, registrationObj.getOrgCreatedBy());
			pstmt.setString(18, registrationObj.getOrgId());

			insertResult = pstmt.executeUpdate();

			if (insertResult != 1) {
				log.error("Insert into un_account_mstr failed for orgId::" + registrationObj.getOrgId());
			}

		} catch (Exception e) {
			log.error("Error in insertAccountMaster() : " + e.getMessage(), e);
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		log.info("Exiting from insertAccountMaster()");
		return insertResult;
	}

	@Override
	public int updateAccountMaster(RegistrationMaster registrationObj, final Connection conn) throws Exception {
		log.info("Inside updateAccountMaster() :: OrgId - " + registrationObj.getOrgId());
		int updateCount = 0;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("UPDATE un_account_master SET ");
			sql.append("acc_type = ?, virtual_acc_code = ?, bank_name = ?, bank_acc_no = ?, ");
			sql.append("ifsc_code = ?, branch_name = ?, account_name = ?, party_code = ?, ");
			sql.append("email_id = ?, address = ?, branch_code = ?, payout_type = ?, ");
			sql.append("isactive = ?, member_type = ?, modified_on = ?, modified_by = ? ");
			sql.append("WHERE account_code = ? AND market_code = ?");

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, registrationObj.getAccType());
			pstmt.setString(2, registrationObj.getOrgVirAccNo());
			pstmt.setString(3, registrationObj.getOrgBankName());
			pstmt.setString(4, registrationObj.getOrgBankCode());
			pstmt.setString(5, registrationObj.getOrgIFSCCode());
			pstmt.setString(6, registrationObj.getOrgBankBranch());
			pstmt.setString(7, registrationObj.getOrgName());
			pstmt.setString(8, registrationObj.getOrgId());
			pstmt.setString(9, registrationObj.getOrgContactEmail());
			pstmt.setString(10, registrationObj.getOrgAddress());
			pstmt.setString(11, registrationObj.getOrgBankBranch());
			pstmt.setString(12, OrgConstants.PAYOUT_TYPE);
			pstmt.setString(13, OrgConstants.IS_ACTIVE);
			pstmt.setString(14, OrgConstants.ACCOUNT_TYPE.MEMBER);
			pstmt.setTimestamp(15, OrgUtil.getCurrentTimestamp());
			pstmt.setString(16, registrationObj.getOrgId());
			pstmt.setString(17, registrationObj.getOrgId());
			pstmt.setString(18, registrationObj.getOrgBaseMarket());

			updateCount = pstmt.executeUpdate();
			if (updateCount != 1) {
				log.error("update into un_account_mstr failed for OrgId - " + registrationObj.getOrgId());
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
					log.error("Error in the time of closing PreparedStatement : " + e.getMessage());
				}
			log.info("Exiting from updateAccountMaster()");
		}

		return updateCount;

	}

	@Override
	public boolean createDefaultUserRole(RegistrationMaster registrationObj, final Connection conn) throws Exception {
		log.info("Inside createDefaultUserRole() for user : " + "U" + registrationObj.getOrgId());
		String userId = "U" + registrationObj.getOrgId().toUpperCase();
		registrationObj.setUserId(userId);
		registrationObj.setReturnMsg("");
		int insertCount = 0;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO un_user_role_mapping " + "(user_id, role_id, is_active, created_by, created_on) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, OrgConstants.DEFAULT_ROLE_STRING);
			pstmt.setString(3, OrgConstants.IS_ACTIVE);
			pstmt.setString(4, registrationObj.getOrgId());
			pstmt.setTimestamp(5, OrgUtil.getCurrentTimestamp());

			insertCount = pstmt.executeUpdate();
			return insertCount > 0;
		} catch (Exception e) {
			log.error("Error in createDefaultUserRole()::" + e.getMessage());
			return false;
		}
	}

	@Override
	public synchronized String saveMessageTracker(MessageTracker msg) {
		log.info("Inside saveMessageTracker() - OrgDaoImpl: Mobile No - " + msg.getMobileNo());
		int rowInserted = 0;
		String returnMessage = OrgConstants.FAILURE_STRING + " Error while Sending SMS"; // Initialize with failure
																							// message
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			String sql = "INSERT INTO MESSAGE_TRACKER (MSG_TRANSCODE, MSG_MESSAGE_TYPE, MSG_PARAMETERS, MSG_MOBILE_NOS, MSG_STATUS, MSG_CREATED_ON, MSG_OWNER) "
					+ "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, msg.getTranscode());
			pstmt.setString(2, msg.getMsgType());
			pstmt.setString(3, msg.getMessage());
			pstmt.setString(4, msg.getMobileNo());
			pstmt.setString(5, msg.getStatus());
			pstmt.setString(6, msg.getOwner());

			rowInserted = pstmt.executeUpdate();

			if (rowInserted > 0) {
				conn.commit();
				log.info("Successfully saved SMS data for MobileNo: " + msg.getMobileNo());
				returnMessage = OrgConstants.SUCCESS_STRING + "SMS Posted successfully to SMS Service.";
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			returnMessage = OrgConstants.FAILURE_STRING + "Error while Sending SMS";
		} finally {
			
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception ex) {
				log.error("Error while closing resources: " + ex.getMessage(), ex);
			}
		}
		
		log.info("Exiting from saveMessageTracker() - OrgDaoImpl: Mobile No - " + msg.getMobileNo());
		return returnMessage;
	}

	@Override
	public RegistrationMaster changeUserPassword(RegistrationMaster registrationObj, final Connection conn)
			throws Exception {

		log.info("inside changeUserPassword() :: UserId -" + registrationObj.getUserId());
		PreparedStatement pstmt = null;
		int rowsAffected = 0;

		try {

			String updateSql = "UPDATE UN_USER_MSTR SET USER_FIRST_LOGIN = ?, USER_ENC_PASSWORD = ?, USER_PASSWORD = ? WHERE USER_ID = ?";
			pstmt = conn.prepareStatement(updateSql);

			// Set parameters
			pstmt.setString(1, OrgConstants.FIRST_LOGIN_FLAG_NO);
			pstmt.setString(2, registrationObj.getEncryptedPassword());
			pstmt.setString(3, registrationObj.getNewPassword());
			pstmt.setString(4, registrationObj.getUserId().toUpperCase());

			rowsAffected = pstmt.executeUpdate();
			// conn.commit();
			// Check if exactly one row was updated
			if (rowsAffected != 1) {
				throw new Exception("Error In Changing the password");
			}

			log.info("Password updated successfully for User ID: " + registrationObj.getUserId());

		} catch (Exception e) {
			log.error("Exception while changing password: " + e.getMessage(), e);
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					log.error("Error closing PreparedStatement: " + e.getMessage(), e);
				}
			}
		}

		log.info("Exiting changeUserPassword()");
		return registrationObj;
	}

	@Override
	public UserMaster fetchUserData(UserMaster userMasterObj) throws Exception {
		log.info("Inside fetchUserData() dao :: USER_ID::" + userMasterObj.getUserId().toUpperCase());
		UserMaster userBean = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT ORG_ID, USER_MOBILE, USER_CATEGORY FROM un_user_mstr WHERE user_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userMasterObj.getUserId().toUpperCase());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				userBean = new UserMaster();
				userBean.setOrgId(rs.getString("ORG_ID"));
				userBean.setUserCategory(rs.getString("USER_CATEGORY"));

				String userMobile = rs.getString("USER_MOBILE");
				if (OrgUtil.isNeitherNullNorEmpty(userMobile)) {
					if (userMobile.length() == 12) {
						userBean.setUserMobile(userMobile.substring(2));
					} else {
						userBean.setUserMobile(userMobile);
					}
				}

			}

		} catch (Exception e) {
			log.error("Exception in fetchUserData(): " + e.getMessage(), e);
			throw e;
		} finally {
			
			try {
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return userBean;
	}

	@Override
	public int updateUserOtp(RegistrationMaster registrationObj, final Connection conn) throws Exception {
		log.info("inside updateUserOTP() :: userId -" + registrationObj.getUserId() + "Otp:"
				+ registrationObj.getPassword());
		PreparedStatement pstmt = null;
		int rowsUpdated = 0;
		try {
			String sql = "UPDATE UN_USER_MSTR SET USER_OTP = ?, USER_OTP_EXPIRY = ? WHERE USER_ID = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, registrationObj.getPassword());
			pstmt.setTimestamp(2, OrgUtil.getOTPExpiryTimestamp());
			pstmt.setString(3, registrationObj.getUserId().toUpperCase());

			rowsUpdated = pstmt.executeUpdate();

			if (rowsUpdated != 1) {
				throw new Exception("Invalid User Id");
			}
			log.info("Successfully updated OTP for userId: " + registrationObj.getUserId());

		} catch (Exception e) {
			log.error("Exception in updateUserOTP(): " + e.getMessage(), e);
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					log.error("Error closing prepared statement: " + e.getMessage(), e);
				}
			}
		}
		log.info("Exiting from updateUserOTP()");
		return rowsUpdated;
	}

	@Override
	public boolean checkValidOTPForUser(RegistrationMaster registrationObj) throws Exception {
		log.info("Inside checkValidOTPForUser() dao :: UserId -" + registrationObj.getUserId());

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean isValid = false;

		try {
			conn = dataSource.getConnection();
			String sql = "SELECT 1 FROM UN_USER_MSTR WHERE USER_ID = ? AND USER_OTP = ? AND USER_OTP_EXPIRY > CURRENT_TIMESTAMP";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, registrationObj.getUserId().toUpperCase());
			pstmt.setString(2, registrationObj.getPassword());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				isValid = true;
			}
		} catch (Exception e) {
			log.error("Error in checkValidOTPForUser(): " + e.getMessage(), e);
			throw e;
		} finally {
			
		}

		return isValid;
	}

}
