package com.taskmanager.auth.service.impl;

import com.taskmanager.auth.dao.TokenDao;
import com.taskmanager.auth.mapper.TokenMapper;
import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.auth.service.AuthService;
import com.taskmanager.common.client.UserServiceClient;
import com.taskmanager.common.constants.ErrorConstants;
import com.taskmanager.common.constants.JwtConstants;
import com.taskmanager.common.enums.Status;
import com.taskmanager.common.exception.ApplicationException;
import com.taskmanager.common.exception.RestCallException;
import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.request.LogoutRequest;
import com.taskmanager.common.util.JwtUtils;
import com.taskmanager.common.util.PasswordUtil;
import com.taskmanager.common.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public JwtToken processLogin(User user) {

        User userDetails = getUserDetails(user);

        // Validate User Status
        if (!userDetails.getStatus().equals(Status.ACTIVE)) {
            throw new ApplicationException(ErrorConstants.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        }

        // Validate Password
        if (PasswordUtil.isMatch(user.getPassword(), userDetails.getPassword())) {
            // Generate JWT Token
            JwtToken jwtToken = jwtUtils.generateJwt(userDetails);
            storeRefreshToken(jwtToken, userDetails, null);
            return jwtToken;

        } else {
            throw new ApplicationException(ErrorConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public JwtToken refreshToken(String refreshToken) {
        Claims claims = getClaims(refreshToken);

        // Validate Token Type and Expiry
        if (claims == null || !jwtUtils.isTokenTypeValid(refreshToken, JwtUtils.TokenType.REFRESH)
                || jwtUtils.isTokenExpired(claims)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Check in DB
        Optional<RefreshToken> storedRefreshTokenOptional = tokenDao.getValidRefreshTokenById(claims.getId());
        if (storedRefreshTokenOptional.isEmpty() || storedRefreshTokenOptional.get().isRevoked()) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        RefreshToken storedRefreshToken = storedRefreshTokenOptional.get();

        // Validate UserId and Token
        if (!storedRefreshToken.getUserId().equals(claims.get(JwtConstants.USER_ID, String.class))
                || !storedRefreshToken.getToken().equals(refreshToken)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Get User Details
        User userDetails = getUserDetails(User.builder()
                .id(storedRefreshToken.getUserId())
                .build());

        if (userDetails == null || !userDetails.getStatus().equals(Status.ACTIVE)) {
            throw new ApplicationException(ErrorConstants.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        }

        // Generate new JWT Token
        JwtToken jwtToken = jwtUtils.generateJwt(userDetails);

        storeRefreshToken(jwtToken, userDetails, storedRefreshToken);

        // Update Refresh Token in DB
        tokenDao.updateRevokedStatus(storedRefreshToken);

        return jwtToken;
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {

        String refreshToken = logoutRequest.getRefreshToken();

        Claims claims = getClaims(refreshToken);

        // Validate Token Type
        if (claims == null || !jwtUtils.isTokenTypeValid(refreshToken, JwtUtils.TokenType.REFRESH)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        Optional<RefreshToken> storedRefreshTokenOptional = tokenDao.getValidRefreshTokenById(claims.getId());

        if (storedRefreshTokenOptional.isEmpty() || storedRefreshTokenOptional.get().isRevoked()) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        RefreshToken storedRefreshToken = storedRefreshTokenOptional.get();

        // Validate UserId and Token
        if (!storedRefreshToken.getUserId().equals(claims.get(JwtConstants.USER_ID, String.class))
                || !storedRefreshToken.getToken().equals(refreshToken)) {
            throw new ApplicationException(ErrorConstants.ERROR_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // Update revoked status
        tokenDao.updateRevokedStatus(storedRefreshToken);

    }

    private Claims getClaims(String refreshToken) {
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
        return claims;
    }

    private void storeRefreshToken(JwtToken jwtToken, User userDetails, RefreshToken existingRefreshToken) {
        // Save Refresh Token
        Claims claims = jwtUtils.extractClaims(jwtToken.getRefreshToken());
        RefreshToken refreshToken = tokenMapper.mapToRefreshToken(jwtToken, userDetails, claims);
        refreshToken.setRefreshCount(existingRefreshToken != null ? existingRefreshToken.getRefreshCount() + 1 : 0);
        refreshToken.setPreviousJti(existingRefreshToken != null ? existingRefreshToken.getId() : null);
        tokenDao.save(refreshToken);
    }

    private User getUserDetails(User userBase) {
        try {
            if (userBase.getId() != null) {
                return userServiceClient.getUserDetailsById(userBase.getId(), true);
            }
            return userServiceClient.getUserDetailsByUsername(userBase.getUsername(), true);
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
