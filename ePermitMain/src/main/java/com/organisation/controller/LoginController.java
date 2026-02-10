package com.organisation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epermit.register.dto.LogOutDto;
import com.epermit.register.dto.LoginDto;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/new")
public class LoginController {
	
	@Autowired
	private LoginService ls;
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	@PostMapping("/login")
	public ApiResponses login(@RequestBody LoginDto model, HttpServletRequest request ) throws Exception {
		ResponseBean responseBean=new ResponseBean();
		try {
			String ip=request.getHeader("X-Forwarded-For");
			if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			            ip = ((HttpServletRequest) request).getHeader("X-Real-IP");
			        }       
			        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			            ip = request.getRemoteAddr();
			        }
			        if (ip != null && ip.contains(",")) {
			            ip = ip.split(",")[0].trim();
			        }			

			ls.login(responseBean, model, ip);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.error("Something went wrong");
			
		}		
		return responseBean.getResponse();
	}
	
	
	//logout
	
	@PostMapping("/logout")
	public ApiResponses logout(@RequestBody LogOutDto model, HttpServletRequest request,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		ResponseBean responseBean = new ResponseBean();

	    try {
	        String token = "";

	        // Extract token
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            token = authHeader.substring(7);
	        }

	        // Fetch IP address
	        String ip = request.getHeader("X-Forwarded-For");
	        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
	            ip = request.getHeader("X-Real-IP");
	        }
	        if (ip == null || ip.isEmpty()) {
	            ip = request.getRemoteAddr();
	        }

	        // Call service
	        ls.logoutUser(model, responseBean, token, ip);

	    } catch (Exception ex) {
	        
	        responseBean.AllResponse("Error", null);
	    }

	    return responseBean.getResponse();
	}

	
	
}
	
	