package com.taskmanager.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.auth.entity.RefreshTokensEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokensEntity, String> {

}
