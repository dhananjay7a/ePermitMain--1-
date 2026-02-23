package com.organisation.controller;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.register.dto.DistrictPinCodeResponseDTO;
import com.epermit.register.dto.DistrictResponseDTO;
import com.epermit.register.dto.PinCodeRequestDTO;
import com.epermit.register.dto.StateRequestDTO;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.model.DistrictPinCodeMstr;
import com.organisation.model.FormOne;
import com.organisation.model.FormThree;
import com.organisation.model.OrgConstitution;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.StateDistrictMaster;
import com.organisation.model.StateMaster;
import com.organisation.repository.OrgConstitutionRepository;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.StateDistrictRepository;
import com.organisation.repository.StateRepository;
import com.organisation.service.PublicService;

@RestController
@RequestMapping("/public")
public class PublicController {

	@Autowired
	private StateRepository stateRepo;

	@Autowired
	private StateDistrictRepository distRepo;

	@Autowired
	private PublicService publicService;

	@Autowired
	private FormOne formService;

	@Autowired
	private FormThree formThreeService;

	@Autowired
	private RegistrationMstrRepository regRepo;

	private static final Logger log = LoggerFactory.getLogger(PublicController.class);

	@GetMapping("/getAllStates")
	public ResponseEntity<List<StateMaster>> getAllState() {
		log.info("Inside getAllState():: publicController");
		try {
			List<StateMaster> states = stateRepo.findAll();
			log.info("Exiting from getAllState():: publicController");
			return ResponseEntity.ok(states);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/getAllDistricts")
	public ResponseEntity<List<DistrictResponseDTO>> getAllDistrict(@RequestBody StateRequestDTO request) {
		log.info("Inside getAllDistrict():: publicController");
		try {

			List<StateDistrictMaster> districts = distRepo.findByIdStateCode(request.getStateCode());
			List<DistrictResponseDTO> response = districts.stream()
					.map(d -> new DistrictResponseDTO(d.getId().getDistrictCode(), d.getDistrictName()))
					.collect(Collectors.toList());

			log.info("Exiting from getAllDistrict():: publicController");
			return ResponseEntity.ok(response);

		} catch (Exception e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/getAllPinCodes")
	public ResponseEntity<ApiResponses> getAllPinCodes(@RequestBody PinCodeRequestDTO request) {
		log.info("Inside getAllPinCodes():: publicController");
		ResponseBean responseBean = new ResponseBean();
		try {
			List<DistrictPinCodeResponseDTO> pinCodes = publicService
					.getPinCodesByStateCodeAndDistrictCode(request.getStateCode(), request.getDistrictCode());
			System.out.println("Fetched Pin Codes: " + pinCodes);
			if (pinCodes != null && !pinCodes.isEmpty()) {
				responseBean.AllResponse("Success", pinCodes);
			} else {
				responseBean.AllResponse("NO_DATA_FOUND", null);
				log.warn("No pin codes found for State Code: {}, District Code: {}", request.getStateCode(),
						request.getDistrictCode());
			}
			log.info("Exiting from getAllPinCodes():: publicController");
			return ResponseEntity.ok(responseBean.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
			responseBean.AllResponse("ERROR", null);
			log.error("Error fetching pin codes for State Code: {}, District Code: {}. Exception: {}",
					request.getStateCode(),
					request.getDistrictCode(), e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
		}
	}

	@GetMapping("/getConstitutions")
	public ResponseEntity<?> getConstitution() {
		log.info("Inside getConstitution() :: PublicController");
		try {
			List<OrgConstitution> cons = publicService.getConstitution();

			if (cons == null || cons.isEmpty()) {
				log.warn("No constitutions found");
				return ResponseEntity.noContent().build(); // 204 No Content
			}

			log.info("Exiting from getConstitution() :: PublicController");
			return ResponseEntity.ok(cons);

		} catch (Exception e) {
			log.error("Error in getConstitution()", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while fetching constitutions");
		}
	}

	@GetMapping("/generateForm1/{orgId}")
	public ResponseEntity<?> generateForm1(@PathVariable String orgId) {
		log.info("Inside generateForm1():: OrgController");
		RegistrationMstr regMstr = regRepo.findByOrgId(orgId).orElse(null);
		String filePath = null;
		try {
			filePath = formService.createPdf(regMstr);
		} catch (Exception e) {
			log.error("Error in generateForm1() :: OrgController", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while generating Form 1 PDF");
		}
		return ResponseEntity.ok("Form 1 PDF generated successfully at: " + filePath);
	}

	@GetMapping("/generateForm3/{orgId}")
	public ResponseEntity<?> generateForm3(@PathVariable String orgId) {
		log.info("Inside generateForm3():: PublicController");
		RegistrationMstr regMstr = regRepo.findByOrgId(orgId).orElse(null);
		String filePath = null;
		try {
			filePath = formThreeService.createPdf(regMstr);
		} catch (Exception e) {
			log.error("Error in generateForm3() :: PublicController", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while generating Form 3 PDF");
		}
		return ResponseEntity.ok("Form 3 PDF generated successfully at: " + filePath);
	}
}
