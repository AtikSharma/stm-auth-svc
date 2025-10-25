package com.taskmanager.auth.repository;

import com.taskmanager.auth.entity.RefreshTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokensEntity, String> {

    @Query("SELECT r FROM RefreshTokensEntity r WHERE r.userId = :id AND r.expiryDateTime > CURRENT_TIMESTAMP ORDER BY r.createdDateTime DESC")
    Optional<RefreshTokensEntity> findLastValidRefreshTokenByUserId(String id);

}
