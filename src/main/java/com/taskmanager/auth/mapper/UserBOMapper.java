package com.taskmanager.auth.mapper;

import java.util.Date;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.UserExp;
import com.taskmanager.common.model.request.LoginRequest;
import com.taskmanager.common.model.request.RegistrationRequest;

@Mapper(componentModel = "spring")
public interface UserBOMapper {

	public RegistrationRequest mapToRegistrationRequestFromUserBase(UserBase userBase);

	public UserBase mapFromRegistrationRequestToUserBase(RegistrationRequest registrationRequest);

	public UserExp mapToExpFromUserBase(UserBase user);

	public UserBase mapFromLoginRequest(LoginRequest loginRequest);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userId", source = "userDetails.id")
	@Mapping(target = "token", source = "jwtResponse.refreshToken")
	public RefreshToken mapToRefreshToken(JwtToken jwtResponse, UserBase userDetails, Date createdDateTime,
			Date expiryDateTime);
}
