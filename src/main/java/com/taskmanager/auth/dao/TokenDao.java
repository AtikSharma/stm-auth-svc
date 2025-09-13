package com.taskmanager.auth.dao;

import com.taskmanager.auth.model.RefreshToken;

import java.util.Optional;


public interface TokenDao {

    public RefreshToken save(RefreshToken refreshToken);

    public Optional<RefreshToken> getValidRefreshTokenById(String id);

    void delete(RefreshToken storedRefreshToken);
}
