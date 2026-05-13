package com.organisation.service;

import com.epermit.register.dto.LogOutDto;
import com.epermit.register.dto.LoginDto;
import com.organisation.responsehandler.ApiResponses;
import com.organisation.responsehandler.ResponseBean;

public interface LoginService {
    void login(ResponseBean responseBean, LoginDto dto, String ip) throws Exception;
    void logoutUser(LogOutDto model, ResponseBean responseBean, String token, String ip);
}