package com.taskmanager.auth.mapper;

import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.auth.model.response.LoginResponse;
import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.UserBase;
import io.jsonwebtoken.Claims;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    @Mapping(target = "id", source = "claims.id")
    @Mapping(target = "userId", source = "userDetails.id")
    @Mapping(target = "token", source = "jwtResponse.refreshToken")
    @Mapping(target = "createdDateTime", source = "claims.issuedAt", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "expiryDateTime", source = "claims.expiration", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "revoked", constant = "false")
    RefreshToken mapToRefreshToken(JwtToken jwtResponse, UserBase userDetails, Claims claims);

    LoginResponse mapToLoginResponse(JwtToken jwtToken);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}


