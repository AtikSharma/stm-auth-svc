package com.taskmanager.auth.model.response;

import com.taskmanager.common.model.ServiceResponse;
import com.taskmanager.common.model.UserExp;

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
public class RegistrationResponse extends ServiceResponse {

	private UserExp userDetails;

}
