package com.taskmanager.auth.service.impl;

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
import com.taskmanager.common.model.request.RegistrationRequest;
import com.taskmanager.common.util.JwtUtils;
import com.taskmanager.common.util.PasswordUtil;
import com.taskmanager.common.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

	private UserServiceClient userServiceClient;
	private UserBOMapper userBOMapper;
	private JwtUtils jwtUtils;
	private TokenDao tokenDao;

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AuthServiceImpl(UserServiceClient userServiceClient, UserBOMapper userBOMapper, JwtUtils jwtUtils,
			TokenDao tokenDao) {
		this.userServiceClient = userServiceClient;
		this.userBOMapper = userBOMapper;
		this.jwtUtils = jwtUtils;
		this.tokenDao = tokenDao;
	}

	@Override
	public UserBase registerUser(UserBase userBase) {
		RegistrationRequest request = userBOMapper.mapToRegistrationRequestFromUserBase(userBase);
		return userServiceClient.registerUser(request);
	}

	@Override
	public JwtToken processLogin(UserBase userBase) {

		// TODO : Fetch UserDetail
		String identifier = getIdentifier(userBase);
		User userDetails = getUserDetails(identifier);

		if (!userDetails.getStatus().equals(Status.ACTIVE)) {
			throw new ApplicationException(ErrorConstants.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
		}

		// TODO : Validate Password, if invalid raise exception
		if (PasswordUtil.isMatch(userBase.getPassword(), userDetails.getPassword())) {
			// TODO : If Valid password generate JWT
			JwtToken jwtToken = jwtUtils.generateJwt(userDetails);

			RefreshToken refreshToken = userBOMapper.mapToRefreshToken(jwtToken, userDetails,
					jwtUtils.getCreatedDate(jwtToken.getRefreshToken()),
					jwtUtils.getExpirationDate(jwtToken.getRefreshToken()));

			refreshToken = tokenDao.save(refreshToken);

			return jwtToken;
		} else {
			throw new ApplicationException(ErrorConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);
		}
	}

	private User getUserDetails(String identifier) {
		try {
			return userServiceClient.getUserDetails(identifier);
		} catch (RestCallException e) {
			if (e.getLocalizedMessage().equals(ErrorConstants.ERROR_USER_NOT_FOUND)) {
				throw new ApplicationException(ErrorConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
			}
		} catch (Exception e) {
			logger.error(ErrorConstants.ERROR_WHILE_FINDING_USER, e);
			throw new ApplicationException(ErrorConstants.ERROR_WHILE_FINDING_USER, e);
		}
		return null;
	}

	private String getIdentifier(UserBase userBase) {
		if (userBase.getEmail() == null || StringUtils.isBlank(userBase.getEmail())) {
			return userBase.getUsername();
		} else {
			return userBase.getEmail();
		}
	}

}
