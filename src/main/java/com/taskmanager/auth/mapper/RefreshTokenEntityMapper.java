package com.taskmanager.auth.mapper;

import org.mapstruct.Mapper;

import com.taskmanager.auth.entity.RefreshTokensEntity;
import com.taskmanager.auth.model.RefreshToken;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityMapper {

	public RefreshToken mapFromEntity(RefreshTokensEntity entity);

	public RefreshTokensEntity mapToEntity(RefreshToken refreshToken);
}
