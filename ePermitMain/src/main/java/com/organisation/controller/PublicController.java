package com.organisation.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.register.dto.DistrictPinCodeResponseDTO;
import com.epermit.register.dto.DistrictResponseDTO;
import com.epermit.register.dto.BlockCodeRequestDTO;
import com.epermit.register.dto.BlockCodeResponseDTO;
import com.epermit.register.dto.PinCodeRequestDTO;
import com.epermit.register.dto.StateRequestDTO;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.model.DistrictPinCodeMstr;
import com.organisation.model.Form3A;
import com.organisation.model.Form3B;
import com.organisation.model.Form4;
import com.organisation.model.Form5;
import com.organisation.model.Form7;
import com.organisation.model.Form8;
import com.organisation.model.FormOne;
import com.organisation.model.FormThree;
import com.organisation.model.FormTwo;
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
import org.springframework.web.bind.annotation.RequestParam;

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
	private FormTwo formTwoService;

	@Autowired
	private Form3A form3AService;

	@Autowired
	private Form3B form3BService;

	@Autowired
	private Form4 form4Service;

	@Autowired
	private Form5 form5Service;

	@Autowired
	private Form7 form7Service;

	@Autowired
	private Form8 form8Service;

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

	@PostMapping("/getBlockCodes")
	public ResponseEntity<ApiResponses> getBlockCodes(@RequestBody BlockCodeRequestDTO request) {
		log.info("Inside getBlockCodes():: publicController");
		ResponseBean responseBean = new ResponseBean();
		try {
			List<BlockCodeResponseDTO> blockCodes = publicService
					.getBlockCodesByPinCodeAndDistrictCode(request.getPinCode(), request.getDistrictCode());
			if (blockCodes != null && !blockCodes.isEmpty()) {
				responseBean.AllResponse("Success", blockCodes);
			} else {
				responseBean.AllResponse("NO_DATA_FOUND", null);
				log.warn("No block codes found for Pin Code: {}, District Code: {}", request.getPinCode(),
						request.getDistrictCode());
			}
			log.info("Exiting from getBlockCodes():: publicController");
			return ResponseEntity.ok(responseBean.getResponse());
		} catch (Exception e) {
			responseBean.AllResponse("ERROR", null);
			log.error("Error fetching block codes for Pin Code: {}, District Code: {}. Exception: {}",
					request.getPinCode(), request.getDistrictCode(), e.getMessage());
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
		log.info("Inside generateForm1():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 1", formService::createPdf);
	}

	@GetMapping("/generateForm3/{orgId}")
	public ResponseEntity<?> generateForm3(@PathVariable String orgId) {
		log.info("Inside generateForm3():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 3", formThreeService::createPdf);
	}

	@GetMapping("/generateForm2/{orgId}")
	public ResponseEntity<?> generateForm2(@PathVariable String orgId) {
		log.info("Inside generateForm2():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 2", formTwoService::createPdf);
	}

	@GetMapping("/generateForm3A/{orgId}")
	public ResponseEntity<?> generateForm3A(@PathVariable String orgId) {
		log.info("Inside generateForm3A():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 3A", form3AService::createPdf);
	}

	@GetMapping("/generateForm3B/{orgId}")
	public ResponseEntity<?> generateForm3B(@PathVariable String orgId) {
		log.info("Inside generateForm3B():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 3B", form3BService::createPdf);
	}

	@GetMapping("/generateForm4/{orgId}")
	public ResponseEntity<?> generateForm4(@PathVariable String orgId) {
		log.info("Inside generateForm4():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 4", form4Service::createPdf);
	}

	@GetMapping("/generateForm5/{orgId}")
	public ResponseEntity<?> generateForm5(@PathVariable String orgId) {
		log.info("Inside generateForm5():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 5", form5Service::createPdf);
	}

	@GetMapping("/generateForm7/{orgId}")
	public ResponseEntity<?> generateForm7(@PathVariable String orgId) {
		log.info("Inside generateForm7():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 7", form7Service::createPdf);
	}

	@GetMapping("/generateForm8/{orgId}")
	public ResponseEntity<?> generateForm8(@PathVariable String orgId) {
		log.info("Inside generateForm8():: PublicController");
		return generateAndDownloadPdf(orgId, "Form 8", form8Service::createPdf);
	}

	private ResponseEntity<?> generateAndDownloadPdf(String orgId, String formName,
			Function<RegistrationMstr, String> pdfGenerator) {
		RegistrationMstr regMstr = regRepo.findByOrgId(orgId).orElse(null);
		if (regMstr == null) {
			log.warn("No registration found for orgId: {}", orgId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Registration not found for orgId: " + orgId);
		}

		try {
			String filePath = pdfGenerator.apply(regMstr);
			if (filePath == null || filePath.isBlank()) {
				log.error("PDF generation returned empty path for {} and orgId {}", formName, orgId);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Something went wrong while generating " + formName + " PDF");
			}

			Path pdfPath = Paths.get(filePath);
			if (!Files.exists(pdfPath)) {
				log.error("Generated PDF not found at path: {}", filePath);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Generated PDF file not found for " + formName);
			}

			byte[] pdfBytes = Files.readAllBytes(pdfPath);
			String downloadFileName = pdfPath.getFileName().toString();

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
					.contentLength(pdfBytes.length)
					.body(new ByteArrayResource(pdfBytes));
		} catch (Exception e) {
			log.error("Error while generating/downloading {} for orgId {}", formName, orgId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while generating " + formName + " PDF");
		}
	}

}
