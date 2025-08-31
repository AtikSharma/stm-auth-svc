package com.taskmanager.auth.service;

import com.taskmanager.common.model.JwtToken;
import com.taskmanager.common.model.UserBase;

public interface AuthService {

	public UserBase registerUser(UserBase userBase);

	public JwtToken processLogin(UserBase userBase);
}
