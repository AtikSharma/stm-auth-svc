package com.taskmanager.auth.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskmanager.auth.dao.TokenDao;
import com.taskmanager.auth.entity.RefreshTokensEntity;
import com.taskmanager.auth.mapper.RefreshTokenEntityMapper;
import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.auth.repository.RefreshTokenRepository;

@Component
public class TokenDaoImpl implements TokenDao {

	private RefreshTokenRepository refreshTokenRepository;
	private RefreshTokenEntityMapper refreshTokenEntityMapper;

	@Autowired
	TokenDaoImpl(RefreshTokenRepository refreshTokenRepository, RefreshTokenEntityMapper refreshTokenEntityMapper) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.refreshTokenEntityMapper = refreshTokenEntityMapper;
	}

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		RefreshTokensEntity entity = refreshTokenEntityMapper.mapToEntity(refreshToken);
		entity = refreshTokenRepository.save(entity);
		return refreshTokenEntityMapper.mapFromEntity(entity);
	}

}
