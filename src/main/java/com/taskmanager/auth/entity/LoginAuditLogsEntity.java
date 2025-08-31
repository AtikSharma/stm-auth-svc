package com.taskmanager.auth.entity;

import java.time.LocalDateTime;

import com.taskmanager.common.enums.LoginAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "login_audit_logs")
public class LoginAuditLogsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "user_id", nullable = false, length = 36)
	private String userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private LoginAction action = LoginAction.LOGIN;

	@Column(name = "ip_address", length = 50)
	private String ipAddress;

	@Column(name = "user_agent", length = 255)
	private String user_agent;

	private LocalDateTime timestamp;
}