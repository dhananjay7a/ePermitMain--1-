package com.organisation.service.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.constants.OrgConstants;
import com.organisation.dao.CommonDao;
import com.organisation.dao.impl.IDGeneratorUtilDao;
import com.organisation.model.ConfigMaster;
import com.organisation.model.IdGenerator;
import com.organisation.model.OrgCategoryMaster;
import com.organisation.model.UserSessionBean;
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private CommonDao commonDao;

	private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

	private static Map<String, String> configMap = new HashMap<>();

	@Override
	public synchronized IdGenerator getIdGenerator(String txnType, final Connection conn) {
		log.info("Start getIdGenerator>>> Txn Type :" + txnType);
		IdGenerator idGenerator = null;
		try {
			// Step 1::: get value select *from ID_GENERATOR where TXN_TYPE='EQD';
			idGenerator = IDGeneratorUtilDao.getIdGenerator(txnType, conn);

			txnType += String.format("%010d", idGenerator.getId().intValue());
			idGenerator.setTxnNo(txnType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("End getIdGenerator<<< Txn Type :" + txnType);
		return idGenerator;
	}

	@Override
	public boolean fetchOrgIsMember(String category) throws Exception {
		log.info("Entering service of OrgIsMember() of CommonServiceImpl");

		return this.fetchOrgCategoryMstr(category).isMember();
	}

	@Override
	public OrgCategoryMaster fetchOrgCategoryMstr(String category) {
		OrgCategoryMaster orgMstr = null;
		try {
			if (OrgUtil.isNeitherNullNorEmpty(category)) {
				orgMstr = commonDao.fetchOrgCategoryMstr(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Execption : " + e.getMessage());
		}
		return orgMstr;
	}

	@Override
	public String fetchConfiguration(String key) {
		log.info("Entering service fetchConfiguration() :: commonServiceImpl");
		UserSessionBean user = null;
		String configValue = "";
		try {
			if (configMap.isEmpty()) {
				List<ConfigMaster> configMasterList = new ArrayList<>();
				configMasterList = commonDao.fetchAllConfigurations();
				for (ConfigMaster configMasterObj : configMasterList) {
					configMap.put(configMasterObj.getConf_key(), configMasterObj.getConf_value());
				}
			}
			configValue = configMap.get(key);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			user = null;
		}
		return configValue;
	}

	@Override
	public String fetchConfiguration(String purpose, String category) {
		log.info("Entering Service fetchConfiguration() of CommonServiceImpl");
		String configvalue = "";
		try {
			if (purpose.equals(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION)
					|| purpose.equals(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION_CASH)) {
				configvalue = this.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION + "_" + category);

				if (!OrgUtil.isNeitherNullNorEmpty(configvalue))
					configvalue = this.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION);

			} else if (purpose.equals(OrgConstants.DEPOSIT_PURPOSE.RENEWAL)
					|| purpose.equals(OrgConstants.DEPOSIT_PURPOSE.RENEWAL_CASH)) {
				configvalue = this.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.RENEWAL + "_" + category);
				if (!OrgUtil.isNeitherNullNorEmpty(configvalue))
					configvalue = this.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.RENEWAL);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception:" + e.getMessage());
		} finally {

		}
		return configvalue;
	}

}
