package com.taskmanager.auth.dao;

import com.taskmanager.auth.model.RefreshToken;

public interface TokenDao {

	public RefreshToken save(RefreshToken refreshToken);
}
