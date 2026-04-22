package com.organisation.service;

import com.organisation.dto.AddModuleRequestDTO;
import com.organisation.dto.CreateUserRequestDto;
import com.organisation.dto.UserRoleMapRequestDto;
import com.organisation.dto.UserRoleRequestDto;
import com.organisation.responsehandler.ApiResponses;

public interface UserService {
	ApiResponses createUser(CreateUserRequestDto request);
	ApiResponses getAllUsers();
	ApiResponses getUserRoleMapping(String token, UserRoleRequestDto request);
	 ApiResponses userRoleMapSubmit(String token, UserRoleMapRequestDto request);
	 ApiResponses fetchDistinctRole(String token);
	 ApiResponses fetchMappedList(String roleId, String token);
	 ApiResponses fetchNotMappedList(String roleId, String token);
	 ApiResponses addModule(AddModuleRequestDTO request, String token);
	 ApiResponses deleteModule(AddModuleRequestDTO request, String token);
}
