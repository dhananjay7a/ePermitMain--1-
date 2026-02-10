package com.organisation.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.organisation.model.UserMstr;
import com.organisation.repository.MessageTrackerRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.security.TokenService;
import com.organisation.service.LoginService;

import io.jsonwebtoken.Claims;

import com.epermit.register.dto.LogOutDto;
import com.epermit.register.dto.LoginDto;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMstrRepository repo;

    @Autowired
    private MessageTrackerRepository mtrepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder encoder;
    
    @Autowired
    private TokenService ts;
    
    ResponseBean responseBean = new ResponseBean();
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Override
    public void login(ResponseBean responseBean, LoginDto dto, String ip) throws Exception {

        String userId = dto.getUser_id().trim();        
        String orgId  = userId.startsWith("U")
                            ? userId.substring(1)
                            : userId;                  

        // Find registered user
        Map<String, Object> user = repo.findUserById(userId);
        if (user == null) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        String username = (String) user.get("user_name");
        String mobile   = (String) user.get("user_mobile");

        if (mobile == null || mobile.trim().isEmpty()) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        // Find OTP message for ORG ID
        String rawMsg = mtrepo.findLatestMessage(mobile, orgId);
        if (rawMsg == null || !rawMsg.contains("`")) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        String[] parts = rawMsg.split("`");
        if (parts.length < 2) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        String dbPassword = parts[1].trim();

        // Match OTP/password
        if (!dto.getUser_password().trim().equals(dbPassword)) {
            responseBean.AllResponse("Invalid", null);
            return;
        }
     // ⭐ UNLOCK USER ON LOGIN
        repo.unlockUser(userId);

        // ✅ Generate token using REAL user ID
        String token = tokenService.generateToken(userId, username, "ROLE_USER");
        // ⭐ STORE TOKEN IN DATABASE
        repo.updateToken(userId, token);

        Map<String, Object> loginData = new HashMap<>();
        loginData.put("token", token);
        loginData.put("user_name", username);

        responseBean.AllResponse("Success", loginData);
    }

    @Override
    public void logoutUser(LogOutDto model, ResponseBean responseBean, String token, String ip) {

        try {

            if (token == null || token.isEmpty()) {
                responseBean.AllResponse("TokenMissing", null);
                return;
            }

            Claims claims = ts.validateToken(token);
            if (claims == null) {
                responseBean.AllResponse("TokenInvalid", null);
                return;
            }

            // Extract userId from token
            String userId = claims.getSubject();

            // ❗ Compare body userId with token userId
            if (!userId.equals(model.getUser_id())) {
                responseBean.AllResponse("Invalid", null);
                return;
            }

            // fetch user
            UserMstr user = repo.findByUserId(userId);
            if (user == null) {
                responseBean.AllResponse("Invalid", null);
                return;
            }

            // check token matches stored token
            if (user.getCurrentToken() == null || !token.equals(user.getCurrentToken())) {
                responseBean.AllResponse("TokenInvalid", null);
                return;
            }

            int updated = repo.logoutUser(userId);

            if (updated == 1) {
                responseBean.AllResponse("LogoutSucces", null);
            } else {
                responseBean.AllResponse("LogoutExist", null);
            }


        } catch (Exception ex) {
            log.info("LogoutServiceImpl-logoutUser", ex.getMessage());
            responseBean.AllResponse("Error", null);
        }
    }






}
