package com.taskmanager.auth.dao;

import com.taskmanager.auth.model.RefreshToken;

import java.util.Optional;


public interface TokenDao {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> getValidRefreshTokenById(String id);

    void delete(RefreshToken storedRefreshToken);

    Optional<RefreshToken> getLastValidRefreshTokenByUserId(String id);

    void updateRevokedStatus(RefreshToken storedRefreshToken);
}
