package com.taskmanager.auth.controller;

import com.taskmanager.auth.mapper.TokenMapper;
import com.taskmanager.auth.mapper.UserBOMapper;
import com.taskmanager.auth.model.response.LoginResponse;
import com.taskmanager.auth.service.AuthService;
import com.taskmanager.common.constants.CommonConstants;
import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.ServiceResponse;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.request.LoginRequest;
import com.taskmanager.common.model.request.LogoutRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = CommonConstants.BASE_URL_AUTH_V1)
public class AuthController {

    private final AuthService authService;
    private final UserBOMapper userBOMapper;
    private final TokenMapper tokenMapper;

    @Autowired
    public AuthController(AuthService authService, UserBOMapper userBOMapper, TokenMapper tokenMapper) {
        this.authService = authService;
        this.userBOMapper = userBOMapper;
        this.tokenMapper = tokenMapper;
    }

    @PostMapping(path = CommonConstants.API_AUTH_LOGIN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Login Successful", content = @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = userBOMapper.mapFromLoginRequest(loginRequest);
        JwtToken token = authService.processLogin(user);
        LoginResponse response = tokenMapper.mapToLoginResponse(token);
        return response.build("Success", HttpStatus.OK, response);
    }

    @GetMapping(path = CommonConstants.API_AUTH_REFRESH_TOKEN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token Refreshed Successfully", content = @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader String refreshToken) {
        JwtToken token = authService.refreshToken(refreshToken);
        LoginResponse response = tokenMapper.mapToLoginResponse(token);
        return response.build("Success", HttpStatus.OK, response);
    }

    @PostMapping(path = CommonConstants.API_AUTH_LOGOUT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout Successful")})
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return new ServiceResponse().build("Logout Successful", HttpStatus.OK);
    }

}
