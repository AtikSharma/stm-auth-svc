package com.taskmanager.auth.mapper;

import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.request.LoginRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserBOMapper {

    public UserBase mapFromLoginRequest(LoginRequest loginRequest);

}
