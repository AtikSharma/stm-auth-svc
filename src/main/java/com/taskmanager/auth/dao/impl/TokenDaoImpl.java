package com.taskmanager.auth.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskmanager.auth.dao.TokenDao;
import com.taskmanager.auth.entity.RefreshTokensEntity;
import com.taskmanager.auth.mapper.RefreshTokenEntityMapper;
import com.taskmanager.auth.model.RefreshToken;
import com.taskmanager.auth.repository.RefreshTokenRepository;

import java.util.Optional;

@Component
public class TokenDaoImpl implements TokenDao {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenEntityMapper refreshTokenEntityMapper;

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

    @Override
    public Optional<RefreshToken> getValidRefreshTokenById(String id) {
        return refreshTokenRepository.findById(id).map(refreshTokenEntityMapper::mapFromEntity);
    }

    @Override
    public void delete(RefreshToken storedRefreshToken) {
        refreshTokenRepository.deleteById(storedRefreshToken.getId());
    }

    @Override
    public Optional<RefreshToken> getLastValidRefreshTokenByUserId(String id) {
        return refreshTokenRepository.findLastValidRefreshTokenByUserId(id).map(refreshTokenEntityMapper::mapFromEntity);
    }

    @Override
    public void updateRevokedStatus(RefreshToken storedRefreshToken) {
        storedRefreshToken.setRevoked(true);
        RefreshTokensEntity entity = refreshTokenEntityMapper.mapToEntity(storedRefreshToken);
        refreshTokenRepository.save(entity);
    }

}
