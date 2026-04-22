package com.organisation.dao;

import java.util.List;

import com.organisation.model.ConfigMaster;
import com.organisation.model.OrgCategoryMaster;

public interface CommonDao {

	OrgCategoryMaster fetchOrgCategoryMstr(String category) throws Exception;
	
	 boolean fillOrgCatMasterMap() throws Exception;
	 
	List<ConfigMaster> fetchAllConfigurations();
}
