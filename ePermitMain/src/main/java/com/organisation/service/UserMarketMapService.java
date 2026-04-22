package com.organisation.service;




import java.util.List;

import com.organisation.dto.UserMappingDto;

public interface UserMarketMapService {

    List<UserMappingDto> fetchAllUserMarketMap(UserMappingDto userMaster, boolean onlyActiveMapping, String token);

    UserMappingDto saveUserMarketMap(UserMappingDto usermarket, String token);
}
