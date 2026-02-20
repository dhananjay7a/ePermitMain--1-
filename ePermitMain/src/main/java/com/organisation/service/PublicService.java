package com.organisation.service;

import java.util.List;

import com.epermit.register.dto.DistrictPinCodeResponseDTO;
import com.organisation.model.DistrictPinCodeMstr;
import com.organisation.model.OrgConstitution;

public interface PublicService {
	List<OrgConstitution> getConstitution();

	List<DistrictPinCodeResponseDTO> getPinCodesByStateCodeAndDistrictCode(String stateCode, String districtCode);
}
