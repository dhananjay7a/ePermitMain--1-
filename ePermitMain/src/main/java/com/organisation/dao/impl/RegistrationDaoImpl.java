package com.organisation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.organisation.dao.RegistrationDao;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.register.model.RegisterBasicInfo;

@Repository
public class RegistrationDaoImpl implements RegistrationDao {
	
	private static final Logger log = LoggerFactory.getLogger(RegistrationDaoImpl.class);
	@Autowired
    private DataSource dataSource;  
	
	public RegistrationMstr findRegDetailsByOrgId(String orgId) {
		log.info("Inside findRegDetailsByOrgId():: RegistrationDaoImpl - orgId: "+ orgId);
		RegistrationMstr regMaster = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	conn = dataSource.getConnection();
        	String sql = "SELECT * FROM tbl_mst_registration WHERE org_id = ?";
        	pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orgId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                regMaster = new RegistrationMstr();

                regMaster.setOrgId(rs.getString("org_id"));
                regMaster.setOrgName(rs.getString("org_name"));
          
                regMaster.setUnifiedLicense(rs.getBoolean("is_unified"));
                regMaster.setOrgCategory(rs.getString("org_category"));
                regMaster.setOrgBaseMarket(rs.getString("org_base_market"));
        
            }
            
        } catch(Exception e) {
        	log.error("Exception : "+ e.getMessage());
        } finally {
        	if (rs != null) { 
                try { rs.close(); } catch (SQLException ignored) {} 
            }
            if (pstmt != null) { 
                try { pstmt.close(); } catch (SQLException ignored) {} 
            }
            if (conn != null) { 
                try { conn.close(); } catch (SQLException ignored) {} 
            }
        }
		log.info("Exit from findRegDetailsByOrgId():: RegistrationDaoImpl.");
		return regMaster;
	}

	public boolean saveDraftBasicInfo(RegisterBasicInfo basicInfoObj, Connection conn) {
		log.info("Inside saveDraftBasicInfo():: RegDaoImpl - orgId: "+ basicInfoObj.getOrgId());
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO nic_register_basic_info ("
				+ "org_request_status, unified_license, org_id, user_id, org_category, org_name, org_type, "
				+ "org_consttn_date, org_base_market, org_doc_type, org_doc_no, reg_fee_validity, "
				+ "created_on, created_by, updated_by) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP , ?, ?)";

		basicInfoObj.setCreatedBy(basicInfoObj.getUserId()); 
		basicInfoObj.setUpdatedBy(basicInfoObj.getUserId()); 
		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, basicInfoObj.getOrgRequestStatus());
			pstmt.setBoolean(2, basicInfoObj.isUnifiedLicense());
			pstmt.setString(3, basicInfoObj.getOrgId());
			pstmt.setString(4, basicInfoObj.getUserId());
			pstmt.setString(5, basicInfoObj.getOrgCategory());
			pstmt.setString(6, basicInfoObj.getOrgName());
			pstmt.setString(7, basicInfoObj.getOrgType());

			// Convert java.util.Date to java.sql.Date or java.sql.Timestamp
			if (basicInfoObj.getOrgConsttnDate() != null) {
			    pstmt.setDate(8, new java.sql.Date(basicInfoObj.getOrgConsttnDate().getTime()));
			} else {
			    pstmt.setNull(8, java.sql.Types.DATE);
			}


			pstmt.setString(9, basicInfoObj.getOrgBaseMarket());
			pstmt.setString(10, basicInfoObj.getOrgDocType()); 
			pstmt.setString(11, basicInfoObj.getOrgDocNo());

			if (basicInfoObj.getRegFeeValidity() != null) {
			    pstmt.setDate(12, new java.sql.Date(basicInfoObj.getRegFeeValidity().getTime()));
			} else {
			    pstmt.setNull(12, java.sql.Types.DATE);
			}

			pstmt.setString(13, basicInfoObj.getCreatedBy());
			pstmt.setString(14, basicInfoObj.getUpdatedBy());

			int rowsInserted = pstmt.executeUpdate();

			return rowsInserted > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
