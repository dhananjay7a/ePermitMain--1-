package com.organisation.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.register.dto.DistrictResponseDTO;
import com.epermit.register.dto.StateRequestDTO;
import com.organisation.model.OrgConstitution;
import com.organisation.model.StateDistrictMaster;
import com.organisation.model.StateMaster;
import com.organisation.repository.OrgConstitutionRepository;
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

	

	
}
