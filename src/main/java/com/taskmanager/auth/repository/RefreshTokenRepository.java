package com.taskmanager.auth.repository;

import com.taskmanager.auth.entity.RefreshTokensEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshTokensEntity, String> {

    @Query(value = "{'userId' : ?0, 'expiryDateTime' : { $gt : ?1 }}", sort = "{'createdDateTime' : -1 }")
    Optional<RefreshTokensEntity> findLastValidRefreshTokenByUserId(String id);

}
