package com.organisation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organisation.model.RegistrationMaster;
import com.organisation.model.ResponseMessage;
import com.organisation.service.EnrollmentService;

@RestController
@RequestMapping("/enroll")
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollService;

	private static final Logger log = LoggerFactory.getLogger(EnrollmentController.class);

	@PostMapping("/enrollOrganization")
	public ResponseEntity<RegistrationMaster> enrollOrganization(@RequestBody RegistrationMaster registrationObj) {
		log.info("Inside enrollment() :: EnrollmentController");
		try {
			 registrationObj = enrollService.enrollOrganization(registrationObj);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Something went wrong");
		}

		log.info("Exiting from enrollOrganization():: EnrollmentController");
		return ResponseEntity.ok(registrationObj);

	}

}
