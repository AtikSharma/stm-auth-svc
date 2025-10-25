package com.taskmanager.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokensEntity {

	@Id
	private String id;

	@Column(name = "user_id", nullable = false, length = 36)
	private String userId;

	@Column(nullable = false, length = 512)
	private String token;

	@Column(name = "expiry_date", nullable = false)
	private LocalDateTime expiryDateTime;

	@Column(name = "created_at")
	private LocalDateTime createdDateTime;

	@Column(name = "revoked", nullable = false)
	private boolean revoked;

	@Column(name = "previous_jti", nullable = true)
	private String previousJti;

	@Column(name = "refresh_count", nullable = false)
	private int refreshCount;
}
