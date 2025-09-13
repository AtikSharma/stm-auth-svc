package com.taskmanager.auth.service.impl;

import com.taskmanager.auth.mapper.TokenMapper;
import com.taskmanager.common.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.taskmanager.auth.dao.TokenDao;
import com.taskmanager.auth.mapper.UserBOMapper;
import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.auth.service.AuthService;
import com.taskmanager.common.client.UserServiceClient;
import com.taskmanager.common.constants.ErrorConstants;
import com.taskmanager.common.enums.Status;
import com.taskmanager.common.exception.ApplicationException;
import com.taskmanager.common.exception.RestCallException;
import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.util.JwtUtils;
import com.taskmanager.common.util.PasswordUtil;
import com.taskmanager.common.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtUtils jwtUtils;
    private final TokenDao tokenDao;
    private final TokenMapper tokenMapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AuthServiceImpl(UserServiceClient userServiceClient, JwtUtils jwtUtils,
                    TokenDao tokenDao, TokenMapper tokenMapper) {
        this.userServiceClient = userServiceClient;
        this.jwtUtils = jwtUtils;
        this.tokenDao = tokenDao;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public JwtToken processLogin(UserBase userBase) {

        User userDetails = getUserDetails(userBase);

        // Validate User Status
        if (!userDetails.getStatus().equals(Status.ACTIVE)) {
            throw new ApplicationException(ErrorConstants.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        }

        // Validate Password
        if (PasswordUtil.isMatch(userBase.getPassword(), userDetails.getPassword())) {
            // Generate JWT Token
            JwtToken jwtToken = jwtUtils.generateJwt(userDetails);
            storeRefreshToken(jwtToken, userDetails);
            return jwtToken;

        } else {
            throw new ApplicationException(ErrorConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public JwtToken refreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Validate Refresh Token and Extract Claims
        Claims claims = null;
        try {
            claims = jwtUtils.extractClaims(refreshToken);
        } catch (JwtException e) {
            throw new ApplicationException(ErrorConstants.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

        // Validate Token Type and Expiry
        if (claims == null || !jwtUtils.isTokenTypeValid(refreshToken, JwtUtils.TokenType.REFRESH)
                || jwtUtils.isTokenExpired(claims)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Check in DB
        RefreshToken storedRefreshToken = tokenDao.getValidRefreshTokenById(claims.getId())
                .orElseThrow(() -> new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));

        // Validate UserId and Token
        if (!storedRefreshToken.getUserId().equals(claims.get(JwtConstants.USER_ID, String.class))
                || !storedRefreshToken.getToken().equals(refreshToken)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Get User Details
        User userDetails = getUserDetails(UserBase.builder()
                .id(storedRefreshToken.getUserId())
                .build());

        if (userDetails == null || !userDetails.getStatus().equals(Status.ACTIVE)) {
            throw new ApplicationException(ErrorConstants.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        }

        // Generate new JWT Token
        JwtToken jwtToken = jwtUtils.generateJwt(userDetails);

        // Update Refresh Token in DB
        tokenDao.delete(storedRefreshToken);
        storeRefreshToken(jwtToken, userDetails);

        return jwtToken;
    }

    private void storeRefreshToken(JwtToken jwtToken, User userDetails) {
        // Save Refresh Token
        Claims claims = jwtUtils.extractClaims(jwtToken.getRefreshToken());
        RefreshToken refreshToken = tokenMapper.mapToRefreshToken(jwtToken, userDetails, claims);
        tokenDao.save(refreshToken);
    }

    private User getUserDetails(UserBase userBase) {
        try {
            if (userBase.getId() != null) {
                return userServiceClient.getUserDetailsById(userBase.getId());
            }
            return userServiceClient.getUserDetailsByUsername(userBase.getUsername());
        } catch (RestCallException e) {
            if (e.getLocalizedMessage().equals(ErrorConstants.ERROR_USER_NOT_FOUND_USERNAME)) {
                throw new ApplicationException(ErrorConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
            } else if (e.getLocalizedMessage().equals(ErrorConstants.ERROR_USER_NOT_FOUND_ID)) {
                throw new ApplicationException(ErrorConstants.INVALID_USER_ID);
            }
        } catch (Exception e) {
            logger.error(ErrorConstants.ERROR_WHILE_FINDING_USER, e);
            throw new ApplicationException(ErrorConstants.ERROR_WHILE_FINDING_USER, e);
        }
        throw new ApplicationException(ErrorConstants.ERROR_WHILE_FINDING_USER);
    }

}
