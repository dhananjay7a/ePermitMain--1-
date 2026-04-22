package com.organisation.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.constants.OrgConstants;
import com.organisation.dto.AddModuleRequestDTO;
import com.organisation.dto.CreateUserRequestDto;
import com.organisation.dto.ModuleResponseDTO;
import com.organisation.dto.UserRoleMapRequestDto;
import com.organisation.dto.UserRoleRequestDto;
import com.organisation.dto.UserRoleResponseDto;
import com.organisation.model.MessageTracker;
import com.organisation.model.ModulesMstr;
import com.organisation.model.RegistrationMstr;
import com.organisation.model.RoleModuleMap;
import com.organisation.model.RoleModuleMapId;
import com.organisation.model.UserMstr;
import com.organisation.model.UserRole;
import com.organisation.model.UserRoleMapping;
import com.organisation.repository.MessageTrackerRepository;
import com.organisation.repository.ModuleMstrRepository;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.RoleModuleMapRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.repository.UserRoleMappingRepository;
import com.organisation.repository.UserRoleRepository;
import com.organisation.responsehandler.ApiResponses;
import com.organisation.responsehandler.ResponseBean;
import com.organisation.security.TokenService;
import com.organisation.service.PasswordService;
import com.organisation.service.UserService;
import com.organisation.util.OrgUtil;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMstrRepository userRepository;

    @Autowired
    private RegistrationMstrRepository registrationRepository;

    @Autowired
    private ResponseBean responseBean;
    
    @Autowired
	private MessageTrackerRepository msgTrackerRepo;
    
    @Autowired
    private UserRoleRepository userRoleRepo;
    @Autowired
    private TokenService ts;
    
    @Autowired
    private UserRoleMappingRepository userRoleMappingrepo;
    
    @Autowired
    private ModuleMstrRepository moduleRepo;
    @Autowired
    private RoleModuleMapRepository roleModuleRepo;
    
    @Override
    public ApiResponses createUser(CreateUserRequestDto request) {

        try {

            // 1️⃣ Check userId in user table
            UserMstr existingUser = userRepository.findByUserId(request.getUserId());

            if (existingUser != null) {
                responseBean.AllResponse("Exists", "UserId already exists");
                return responseBean.getResponse();
            }

            // 2️⃣ Check orgId in registration table
            Optional<RegistrationMstr> existingReg =
                    registrationRepository.findByOrgId(request.getOrgId());

            if (existingReg.isPresent()) {
                responseBean.AllResponse("Exists", "OrgId already exists in registration");
                return responseBean.getResponse();
            }

            // 3️⃣ Create USER
            UserMstr user = new UserMstr();

            user.setUserId(request.getUserId());
            user.setUserName(request.getUserName());
            user.setUserMobile(request.getContactNumber());
            user.setUserEmail(request.getEmail());
            user.setOrgId(request.getOrgId());

            user.setCreatedBy(request.getCreatedBy());
            user.setCreatedOn(LocalDateTime.now());

            user.setIsLock("N");
            user.setNoOfAttempt(0);
            user.setUserIsActive("Y");
            user.setFirstLoginFlag("Y");

            String defaultPassword =
                    OrgUtil.getAlphaNumericOtp(OrgConstants.DEFAULT_PASSWORD_SIZE);

            user.setUserPassword(
                    PasswordService.encrypt(defaultPassword)
            );

            userRepository.save(user);

            // 4️⃣ Create REGISTRATION
            RegistrationMstr registration = new RegistrationMstr();

            registration.setOrgId(request.getOrgId());
            registration.setCreatedBy(request.getCreatedBy());
            registration.setCreatedOn(LocalDateTime.now());

            registrationRepository.save(registration);
            
        	MessageTracker messageTracker = new MessageTracker();
			messageTracker.setMobileNo(request.getContactNumber());
			messageTracker.setTranscode(OrgConstants.SMS_TRANSCODE1);
			messageTracker.setMsgType(OrgConstants.MESSAGE_TYPE.SMS);

			String parameters = registration.getOrgId() + "`" + defaultPassword + "`"
					+ OrgUtil.getOTPExpiryTimestamp();
			messageTracker.setMessage(parameters);

			messageTracker.setStatus("P");
			messageTracker.setRemarks("PENDING");
			messageTracker.setOwner("EPERMIT");
			messageTracker.setCreatedOn(LocalDateTime.now());

			msgTrackerRepo.save(messageTracker);

            // 5️⃣ Response
            Map<String,Object> data = new HashMap<>();
            data.put("userId", request.getUserId());
            data.put("orgId", request.getOrgId());
            data.put("userName",request.getUserName());

            responseBean.AllResponse("Success", data);

        }
        catch (Exception e) {

            log.error("Error creating user", e);

            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }
    
    @Override
    public ApiResponses getAllUsers() {

        try {

            var users = userRepository.getAllUsers();

            if (users.isEmpty()) {
                responseBean.AllResponse("No Data Found", null);
            } else {
                responseBean.AllResponse("Success", users);
            }

        } catch (Exception e) {

            log.error("Error fetching users", e);

            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }

    @Override
    public ApiResponses getUserRoleMapping(String token, UserRoleRequestDto request) {

        try {

            if (token == null || token.isEmpty()) {
                responseBean.AllResponse("TokenMissing", null);
                return responseBean.getResponse();
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            if (request == null || request.getUserId() == null) {
                responseBean.AllResponse("UserIdMissing", null);
                return responseBean.getResponse();
            }

            String userId = request.getUserId();

            log.info("Fetching role mapping for userId {}", userId);

            List<UserRoleResponseDto> list = userRoleRepo.getUserRoleMapping(userId);

            if (list == null || list.isEmpty()) {
                responseBean.AllResponse("NoDataFound", null);
            } else {
                responseBean.AllResponse("Success", list);
            }

        } catch (Exception e) {

            log.error("Error fetching role mapping", e);
            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }
    
    @Override
    public ApiResponses userRoleMapSubmit(String token, UserRoleMapRequestDto request) {
    	
        try {

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate Token
            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            // Extract userId
            String userId = ts.extractUserId(token);

            if (userId == null || userId.isEmpty()) {
                responseBean.AllResponse("TokenMissing", null);
                return responseBean.getResponse();
            }

            

            // Check Duplicate
            boolean exists = userRoleMappingrepo.existsByUserIdAndRoleId(userId, request.getRoleId());

            if (exists) {
                responseBean.AllResponse("Exists", null);
                return responseBean.getResponse();
            }

            // Save Mapping
            UserRoleMapping mapping = new UserRoleMapping();
            mapping.setUserId(request.getUserId());
            mapping.setRoleId(request.getRoleId());
            mapping.setIsActive("T");
            mapping.setCreatedBy(userId);
            mapping.setCreatedOn(LocalDateTime.now());

            userRoleMappingrepo.save(mapping);

            responseBean.AllResponse("Success", null);
            return responseBean.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
            responseBean.AllResponse("Error", e.getMessage());
            return responseBean.getResponse();
        }
    }
    
    @Override
    public ApiResponses fetchDistinctRole(String token) {

        responseBean.reset();

        try {

            log.info("Inside fetchDistinctRole()");

            List<UserRole> roleList = userRoleRepo.fetchDistinctRole();

            if (roleList == null || roleList.isEmpty()) {

                responseBean.AllResponse("NoDataFound", null);

            } else {

                responseBean.AllResponse("Success", roleList);

            }

        } catch (Exception e) {

            log.error("Exception fetchDistinctRole()", e);
            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }
    
    

    @Override
    public ApiResponses fetchMappedList(String roleId, String token) {

        try {

            responseBean.reset();

            // Remove Bearer
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate Token
            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            List<ModulesMstr> modules = moduleRepo.fetchMappedList(roleId);

            if (modules == null || modules.isEmpty()) {
                responseBean.AllResponse("NoDataFound", null);
                return responseBean.getResponse();
            }

            // Convert Entity → DTO
            List<ModuleResponseDTO> dtoList = modules.stream()
                    .map(m -> new ModuleResponseDTO(
                            m.getModuleId(),
                            m.getModuleName()))
                    .collect(Collectors.toList());

            responseBean.AllResponse("Success", dtoList);

        } catch (Exception e) {

            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }
    
    
    @Override
    public ApiResponses fetchNotMappedList(String roleId, String token) {

        try {

            responseBean.reset();

            // Remove Bearer
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate Token
            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            List<ModulesMstr> modules = moduleRepo.fetchNotMappedList(roleId);

            if (modules == null || modules.isEmpty()) {
                responseBean.AllResponse("NoDataFound", null);
                return responseBean.getResponse();
            }

            // Convert Entity → DTO
            List<ModuleResponseDTO> dtoList = modules.stream()
                    .map(m -> new ModuleResponseDTO(
                            m.getModuleId(),
                            m.getModuleName()))
                    .collect(Collectors.toList());

            responseBean.AllResponse("Success", dtoList);

        } catch (Exception e) {

            responseBean.AllResponse("Error", e.getMessage());
        }

        return responseBean.getResponse();
    }

    @Override
    public ApiResponses addModule(AddModuleRequestDTO request, String token) {

        try {

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            String userId = claims.getSubject();

            for (Long moduleId : request.getMenuIds()) {

                RoleModuleMap map = new RoleModuleMap();

                // PRIMARY KEY FIELDS
                map.setMarketCode("DFLT");
                map.setRoleId(request.getRoleId());
                map.setModuleId(moduleId);

                // OTHER FIELDS
                map.setCreatedBy(userId);
                map.setCreatedOn(LocalDateTime.now());
             

                roleModuleRepo.save(map);
            }

            responseBean.InsertResponse("Success");

        } catch (Exception e) {
            e.printStackTrace();
            responseBean.InsertResponse("Error");
        }

        return responseBean.getResponse();
    }
   
    @Override
   public ApiResponses deleteModule(AddModuleRequestDTO request, String token) {

        try {

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = ts.validateToken(token);

            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return responseBean.getResponse();
            }

            roleModuleRepo.deleteModules(
                    request.getRoleId(),
                    request.getMenuIds()
            );

            responseBean.DeleteResponse("Success");

        } catch (Exception e) {
            e.printStackTrace();
            responseBean.DeleteResponse("Error");
        }

        return responseBean.getResponse();
    }
    
}