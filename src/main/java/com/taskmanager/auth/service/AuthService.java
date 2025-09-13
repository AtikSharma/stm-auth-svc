package com.taskmanager.auth.service;

import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.UserBase;

public interface AuthService {

	public JwtToken processLogin(UserBase userBase);

	public JwtToken refreshToken(String refreshToken);
}
