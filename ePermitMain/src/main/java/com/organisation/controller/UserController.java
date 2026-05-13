package com.organisation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organisation.dto.AddModuleRequestDTO;
import com.organisation.dto.CreateUserRequestDto;
import com.organisation.dto.ModuleRequestDTO;
import com.organisation.dto.UserRoleMapRequestDto;
import com.organisation.dto.UserRoleRequestDto;
import com.organisation.responsehandler.ApiResponses;
import com.organisation.responsehandler.ResponseBean;
import com.organisation.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private ResponseBean responseBean;

    @Autowired
    private UserService userService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create")
    public ApiResponses createUser(@RequestBody CreateUserRequestDto request) {

        ApiResponses response = new ApiResponses();

        try {

            if(request == null){
               responseBean.AllResponse("Nulltype", null);
                return response;
            }
           
            return userService.createUser(request);
          

        } catch (Exception e) {

            log.error("Create user error", e);
           
            responseBean.AllResponse("Error", e.getMessage());

            return response;
        }
    }
    
    @GetMapping("/getAllUsers")
    public ApiResponses getAllUsers() {

        return userService.getAllUsers();
    }
    
    
    
    @PostMapping("/userRoleMapping")
    public ApiResponses getUserRoleMapping(
            @RequestHeader("Authorization") String token,
            @RequestBody UserRoleRequestDto request) {

        return userService.getUserRoleMapping(token, request);
    }
    
    @PostMapping("/userRoleMapSubmit")
    public ApiResponses userRoleMapSubmit(
            @RequestHeader("Authorization") String token,
             @RequestBody UserRoleMapRequestDto request) {

        return userService.userRoleMapSubmit(token, request);
    }
    
    
    
    @GetMapping("/fetchDistinctRole")
    public ApiResponses fetchDistinctRole( @RequestHeader("Authorization") String token) {

        log.info("Inside fetchDistinctRole() :: UserController");

        ApiResponses response = userService.fetchDistinctRole(token);

        log.info("Exit fetchDistinctRole() :: UserController");

        return response;
    }
    
    
    @PostMapping("/fetchMappedList")
    public ApiResponses fetchMappedList(
            @RequestBody ModuleRequestDTO requestDto,
            @RequestHeader("Authorization") String token) {

        try {

            return userService.fetchMappedList(requestDto.getRoleId(), token);

        } catch (Exception e) {

            ApiResponses res = new ApiResponses();
            res.setCode("400");
            res.setMessage("Something went wrong");

            return res;
        }
    }
    
    
    @PostMapping("/fetchNotMappedList")
    public ApiResponses fetchNotMappedList(
            @RequestBody ModuleRequestDTO requestDto,
            @RequestHeader("Authorization") String token) {

        try {

            return userService.fetchNotMappedList(requestDto.getRoleId(), token);

        } catch (Exception e) {

            ApiResponses res = new ApiResponses();
            res.setCode("400");
            res.setMessage("Something went wrong");

            return res;
        }
    }
    
    
    @PostMapping("/addModule")
    public ApiResponses addModule(
            @RequestBody AddModuleRequestDTO request,
            @RequestHeader("Authorization") String token) {

        return userService.addModule(request, token);
    }
    
    @DeleteMapping("/deleteModules")
    public ApiResponses deleteModules(
            @RequestBody AddModuleRequestDTO request,
            @RequestHeader("Authorization") String token) {
 
        return userService.deleteModule(request, token);
    }
    
}
