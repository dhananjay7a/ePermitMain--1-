package com.organisation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.organisation.constants.OrgConstants;
import com.organisation.dao.CommonDao;
import com.organisation.model.ConfigMaster;
import com.organisation.model.OrgCategoryMaster;



@Repository
public class CommonDaoImpl implements CommonDao {

	private static final Logger log = LoggerFactory.getLogger(CommonDaoImpl.class);
	private Map<String, OrgCategoryMaster> categoryMasterMap = new HashMap<String, OrgCategoryMaster>();
	
	@Autowired
    private DataSource dataSource;  

	@Override
	public OrgCategoryMaster fetchOrgCategoryMstr(String category) throws Exception {
		OrgCategoryMaster orgCatMaster = null;

		try {
			if (categoryMasterMap.isEmpty()) {
				if (!this.fillOrgCatMasterMap()) {
					return orgCatMaster;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		orgCatMaster = categoryMasterMap.get(category);
		return orgCatMaster;
	}

	@Override
	public synchronized boolean fillOrgCatMasterMap() throws Exception {
		log.info("Inside fillOrgCatMasterMap()");
		Connection conn = null;
		ResultSet rs = null;
		OrgCategoryMaster orgCatMaster = null;
		PreparedStatement pstmt = null;
		try {
			 conn = dataSource.getConnection();
			String sql = "SELECT * FROM un_org_category_mstr";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				orgCatMaster = new OrgCategoryMaster();
				orgCatMaster.setOrgCategory(rs.getString("ORG_CATEGORY"));
				orgCatMaster.setOrgCategoryName(rs.getString("ORG_CATEGORY_NAME"));
				orgCatMaster.setOrgUserScope(rs.getString("ORG_USER_SCOPE"));
				orgCatMaster.setOrgCategoryScope(rs.getString("ORG_CATEGORY_SCOPE"));
				orgCatMaster.setOrgSelfRegAllowed(rs.getString("ORG_SELF_REG_ALLOWED"));
				orgCatMaster.setOrgDfltUserRole(rs.getString("ORG_DFLT_USER_ROLE"));
				orgCatMaster.setCategoryType(rs.getString("CATEGORY_TYPE"));

				switch (orgCatMaster.getCategoryType()) {
				case OrgConstants.CATEGORY_TYPE.HOST:
					orgCatMaster.setHost(true);
					orgCatMaster.setMember(false);
					break;

				case OrgConstants.CATEGORY_TYPE.RMC:
					orgCatMaster.setHost(false);
					orgCatMaster.setMember(false);
					break;

				case OrgConstants.CATEGORY_TYPE.HAAT_BAZAR:
					orgCatMaster.setHost(false);
					orgCatMaster.setMember(true);
					break;

				case OrgConstants.CATEGORY_TYPE.MEMBER:
				default:
					orgCatMaster.setHost(false);
					orgCatMaster.setMember(true);
					break;

				}
				categoryMasterMap.put(orgCatMaster.getOrgCategory(), orgCatMaster);
			}
			return true;
		} finally {
			
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}

	}

	
	@Override
	public List<ConfigMaster> fetchAllConfigurations() {
		log.info("Inside fetchAllConfigurations()");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		List<ConfigMaster> configList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT conf_key, conf_value, remarks FROM un_configurations";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ConfigMaster configMasterObj = new ConfigMaster();
				configMasterObj.setConf_key(rs.getString("conf_key"));
				configMasterObj.setConf_value(rs.getString("conf_value"));
				configMasterObj.setRemarks(rs.getString("remarks"));
				configList.add(configMasterObj);
			}

		} catch (Exception e) {
			log.error("Exception :: " + e.getMessage());
			e.printStackTrace();
		} finally {
			
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return configList;
	}

}
