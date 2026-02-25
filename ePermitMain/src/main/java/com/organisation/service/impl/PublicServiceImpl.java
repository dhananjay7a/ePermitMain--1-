package com.organisation.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epermit.register.dto.BlockCodeResponseDTO;
import com.epermit.register.dto.DistrictPinCodeResponseDTO;
import com.organisation.model.DistrictPinCodeMstr;
import com.organisation.model.OrgConstitution;
import com.organisation.repository.OrgConstitutionRepository;
import com.organisation.repository.PinCodeMstrRepository;
import com.organisation.service.PublicService;

@Service
public class PublicServiceImpl implements PublicService {
	@Autowired
	private OrgConstitutionRepository consRepo;

	@Autowired
	private PinCodeMstrRepository pinCodeRepo;

	private static final Logger log = LoggerFactory.getLogger(PublicServiceImpl.class);

	@Override
	public List<OrgConstitution> getConstitution() {

		log.info("Inside getConstitution():: PublicServiceImpl");

		try {
			return consRepo.findByIsActive("T");
		} catch (Exception e) {
			log.error("Exception :", e);
			throw e;
		}
	}

	@Override
	public List<DistrictPinCodeResponseDTO> getPinCodesByStateCodeAndDistrictCode(String stateCode,
			String districtCode) {
		log.info("Inside getPinCodesByStateCodeAndDistrictCode():: PublicServiceImpl");
		List<DistrictPinCodeResponseDTO> pinCodes = new ArrayList<>();
		try {
			if (stateCode == null || stateCode.isEmpty() || districtCode == null || districtCode.isEmpty()) {
				log.warn("State code or District code is null or empty");
				throw new IllegalArgumentException("State code and District code must be provided");
			}
			System.out.println("State Code: " + stateCode + ", District Code: " + districtCode);
			List<DistrictPinCodeResponseDTO> response = pinCodeRepo.findPinCodesByStateCodeAndDistrictCode(stateCode,
					districtCode);
			/*
			 * for (DistrictPinCodeResponseDTO dto : response) {
			 * log.info("Fetched Pin Code: {}, Block Name: {}", dto.getPinCode(),
			 * dto.getBlockName());
			 * }
			 */
			log.info("Exiting getPinCodesByStateCodeAndDistrictCode():: PublicServiceImpl");
			return response;
		} catch (Exception e) {
			log.error("Exception :", e);
			throw new RuntimeException(
					"Error fetching pin codes for state code: " + stateCode + " and district code: " + districtCode, e);
		}
	}

	@Override
	public List<BlockCodeResponseDTO> getBlockCodesByPinCodeAndDistrictCode(String pinCode, String districtCode) {
		log.info("Inside getBlockCodesByPinCodeAndDistrictCode():: PublicServiceImpl");
		try {
			if (pinCode == null || pinCode.isEmpty() || districtCode == null || districtCode.isEmpty()) {
				log.warn("Pin code or District code is null or empty");
				throw new IllegalArgumentException("Pin code and District code must be provided");
			}

			List<String> blockCodes = pinCodeRepo.findBlockCodesByPinCodeAndDistrictCode(pinCode, districtCode);
			List<BlockCodeResponseDTO> response = blockCodes.stream()
					.filter(code -> code != null && !code.isBlank())
					.map(BlockCodeResponseDTO::new)
					.collect(Collectors.toList());

			log.info("Exiting getBlockCodesByPinCodeAndDistrictCode():: PublicServiceImpl");
			return response;
		} catch (Exception e) {
			log.error("Exception :", e);
			throw new RuntimeException(
					"Error fetching block codes for pin code: " + pinCode + " and district code: " + districtCode, e);
		}
	}

}
