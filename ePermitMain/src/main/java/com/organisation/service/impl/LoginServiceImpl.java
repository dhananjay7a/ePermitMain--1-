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
import com.organisation.responsehandler.ApiResponses;
import com.organisation.responsehandler.ResponseBean;
import com.organisation.security.TokenService;
import com.organisation.service.LoginService;
import com.organisation.service.PasswordService;

import io.jsonwebtoken.Claims;

import com.epermit.register.dto.LogOutDto;
import com.epermit.register.dto.LoginDto;

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

        Map<String, Object> userMap = repo.findUserById(userId);
        if (userMap == null) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        String username = (String) userMap.get("user_name");

        // 🔥 Fetch User Entity for changed password
        UserMstr userEntity = repo.findByUserId(userId);

        boolean passwordMatched = false;

        // 1️⃣ First check changed password
        if (userEntity != null && userEntity.getUserPassword() != null) {

            String encryptedInput =
                    PasswordService.encrypt(dto.getUser_password().trim());

            if (encryptedInput.equals(userEntity.getUserPassword())) {
                passwordMatched = true;
            }
        }

        // 2️⃣ If not matched → check default OTP password
        if (!passwordMatched) {

            String mobile = (String) userMap.get("user_mobile");

            String rawMsg = mtrepo.findLatestMessage(mobile, userId);

            if (rawMsg != null && rawMsg.contains("`")) {

                String[] parts = rawMsg.split("`");

                if (parts.length >= 2) {
                    String dbPassword = parts[1].trim();

                    if (dto.getUser_password().trim().equals(dbPassword)) {
                        passwordMatched = true;
                    }
                }
            }
        }

        // ❌ If still not matched
        if (!passwordMatched) {
            responseBean.AllResponse("Invalid", null);
            return;
        }

        // ✅ Unlock user
        repo.unlockUser(userId);

        // ✅ Generate token
        String token = tokenService.generateToken(userId, username, "ROLE_USER");

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
