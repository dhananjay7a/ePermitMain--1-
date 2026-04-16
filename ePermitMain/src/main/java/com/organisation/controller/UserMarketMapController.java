package com.organisation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epermit.Exception.UnauthorizedException;
import com.epermit.register.responsehandler.ApiResponses;
import com.epermit.register.responsehandler.ResponseBean;
import com.organisation.dto.UserMappingDto;
import com.organisation.service.UserMarketMapService;

import java.util.List;

@RestController
@RequestMapping("/usermarketmap")
public class UserMarketMapController {

    private static final Logger log = LoggerFactory.getLogger(UserMarketMapController.class);

    @Autowired
    private UserMarketMapService userMarketMapService;

    @PostMapping("/fetchAllUserMarketMap")
    public ResponseEntity<ApiResponses> fetchAllUserMarketMap(@RequestBody UserMappingDto userMaster,
            @RequestHeader("Authorization") String token) {
        log.info("Inside fetchAllUserMarketMap() :: userMarketMap controller userId :: " + userMaster.getUserId());
        ResponseBean responseBean = new ResponseBean();

        try {
            List<UserMappingDto> userMarketLst = userMarketMapService.fetchAllUserMarketMap(userMaster, true, token);
            responseBean.AllResponse("Success", userMarketLst);
            log.info("Exit fetchAllUserMarketMap() :: UserMarketMap controller");
            return ResponseEntity.ok(responseBean.getResponse());
        } catch (UnauthorizedException e) {
            log.error("Token validation error: {}", e.getMessage());
            responseBean.AllResponse("TokenInvalid", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBean.getResponse());
        } catch (Exception e) {
            log.error("Error in fetchAllUserMarketMap", e);
            responseBean.AllResponse("Error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
        }
    }

    @PostMapping("/saveUserMarketMap")
    public ResponseEntity<ApiResponses> saveUserMarketMap(@RequestBody UserMappingDto usermarket,
            @RequestHeader("Authorization") String token) {
        log.info("Inside saveUserMarketMap() :: userMarketMap controller  userId :: " + usermarket.getUserId());
        ResponseBean responseBean = new ResponseBean();

        try {
            usermarket = userMarketMapService.saveUserMarketMap(usermarket, token);
            responseBean.AllResponse("Success", usermarket);
            log.info("Exit saveUserMarketMap() :: userMarketMap controller");
            return ResponseEntity.ok(responseBean.getResponse());
        } catch (UnauthorizedException e) {
            log.error("Token validation error: {}", e.getMessage());
            responseBean.AllResponse("TokenInvalid", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBean.getResponse());
        } catch (Exception e) {
            log.error("Error in saveUserMarketMap", e);
            responseBean.AllResponse("Error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBean.getResponse());
        }
    }
}