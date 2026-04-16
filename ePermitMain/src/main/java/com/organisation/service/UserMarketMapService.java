package com.organisation.service;

import com.organisation.dto.UserMappingDto;

import java.util.List;

public interface UserMarketMapService {

    List<UserMappingDto> fetchAllUserMarketMap(UserMappingDto userMaster, boolean onlyActiveMapping, String token);

    UserMappingDto saveUserMarketMap(UserMappingDto usermarket, String token);
}