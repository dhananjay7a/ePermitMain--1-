package com.organisation.service;

import java.sql.Connection;

import com.organisation.model.IdGenerator;
import com.organisation.model.OrgCategoryMaster;

public interface CommonService {

	IdGenerator getIdGenerator(String txnType, final Connection con);
	
	boolean fetchOrgIsMember(String category) throws Exception;
	
	OrgCategoryMaster fetchOrgCategoryMstr(String category);
	
	String fetchConfiguration(String key);
	
	
}
