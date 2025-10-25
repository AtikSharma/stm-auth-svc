package com.taskmanager.auth.service;

import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.request.LogoutRequest;

public interface AuthService {

	JwtToken processLogin(UserBase userBase);

	JwtToken refreshToken(String refreshToken);

	void logout(LogoutRequest refreshToken);
}
