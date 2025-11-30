package com.taskmanager.auth.service;

import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.request.LogoutRequest;

public interface AuthService {

	JwtToken processLogin(User user);

	JwtToken refreshToken(String refreshToken);

	void logout(LogoutRequest refreshToken);
}
