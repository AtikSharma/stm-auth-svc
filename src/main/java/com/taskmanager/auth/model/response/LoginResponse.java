package com.taskmanager.auth.model.response;

import com.taskmanager.common.model.ServiceResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoginResponse extends ServiceResponse {

	private String accessToken;
	private String refreshToken;

}
