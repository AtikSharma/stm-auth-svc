package com.taskmanager.auth.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class RefreshToken {

	private String id;

	private String userId;

	private String token;

	private LocalDateTime expiryDateTime;

	private LocalDateTime createdDateTime;
}
