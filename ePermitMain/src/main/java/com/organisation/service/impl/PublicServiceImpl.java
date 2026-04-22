package com.organisation.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.model.OrgConstitution;
import com.organisation.repository.OrgConstitutionRepository;
import com.organisation.service.PublicService;

@Service
public class PublicServiceImpl implements PublicService {
	@Autowired
	private OrgConstitutionRepository consRepo;

	private static final Logger log = LoggerFactory.getLogger(PublicServiceImpl.class);

	@Override
	public List<OrgConstitution> getConstitution() {

	    log.info("Inside getConstitution():: PublicServiceImpl");

	    try {
	        return consRepo.findByIsActive("T");
	    }
	    catch (Exception e) {
	        log.error("Exception :", e);
	        throw e;
	    }
	}

}
