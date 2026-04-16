package com.organisation.service.impl;

import com.epermit.Exception.UnauthorizedException;
import com.organisation.dto.UserMappingDto;
import com.organisation.model.UserMarketMapping;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.repository.UserMarketMapRepository;
import com.organisation.security.TokenService;
import com.organisation.service.UserMarketMapService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserMarketMapServiceImpl implements UserMarketMapService {

    private static final Logger log = LoggerFactory.getLogger(UserMarketMapServiceImpl.class);

    @Autowired
    private UserMarketMapRepository userMarketMapRepository;

    @Autowired
    private RegistrationMstrRepository registrationMstrRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public List<UserMappingDto> fetchAllUserMarketMap(UserMappingDto userMaster, boolean onlyActiveMapping,
            String token) {
        log.info("Inside fetchAllUserMarketMap() in UserMarketMapServiceImpl");

        // Token validation
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = tokenService.validateToken(token);
        if (claims == null) {
            throw new UnauthorizedException("Invalid Token");
        }

        try {
            String isActive = onlyActiveMapping ? "T" : "F";
            return userMarketMapRepository.fetchAllUserMarketMap(userMaster.getUserId(),
                    isActive);
        } catch (Exception e) {
            log.error("Error in fetchAllUserMarketMap", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserMappingDto saveUserMarketMap(UserMappingDto usermarket, String token) {
        log.info("Inside saveUserMarketMap() in UserMarketMapServiceImpl");

        // Token validation
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = tokenService.validateToken(token);
        if (claims == null) {
            throw new UnauthorizedException("Invalid Token");
        }

        try {
            // Check if exists
            if (userMarketMapRepository.existsByUserIdAndMarketCode(usermarket.getUserId(),
                    usermarket.getMarketCode())) {
                usermarket.setErrorCode(1);
                usermarket.setReturnMsg("User market mapping already exists.");
                return usermarket;
            }

            // Create entity
            UserMarketMapping entity = new UserMarketMapping();
            entity.setUserId(usermarket.getUserId());
            entity.setMarketCode(usermarket.getMarketCode());
            entity.setIsActive("T");
            entity.setCreatedBy(usermarket.getCreatedBy());
            entity.setCreatedOn(LocalDateTime.now());

            userMarketMapRepository.save(entity);

            // Archive and update if orgId present
            if (usermarket.getOrgId() != null) {
                registrationMstrRepository.findByOrgId(usermarket.getOrgId()).ifPresentOrElse(reg -> {
                    reg.setOrgBaseMarket(usermarket.getMarketCode());
                    registrationMstrRepository.save(reg);
                    usermarket.setReturnMsg("User market mapping Updated Successfully.");
                    usermarket.setErrorCode(0);
                }, () -> {
                    usermarket.setReturnMsg("Organization not found for orgId: " + usermarket.getOrgId());
                    usermarket.setErrorCode(2);
                });
            } else {
                usermarket.setReturnMsg("org Id is null");
                usermarket.setErrorCode(2);
            }

            return usermarket;
        } catch (Exception e) {
            log.error("Error in saveUserMarketMap", e);
            usermarket.setErrorCode(2);
            usermarket.setReturnMsg("Error while saving user market mapping");
            throw e;
        }
    }
}