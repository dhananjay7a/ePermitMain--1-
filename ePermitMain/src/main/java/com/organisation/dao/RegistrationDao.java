package com.organisation.dao;

import java.sql.Connection;

import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.register.model.RegisterBasicInfo;

public interface RegistrationDao {
	
	RegistrationMstr findRegDetailsByOrgId(String orgId);
	
	boolean saveDraftBasicInfo(RegisterBasicInfo basicInfoObj, Connection conn);
	
	
}
