package com.taskmanager.auth.entity;

import com.taskmanager.common.enums.LoginAction;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "login_audit_logs")
public class LoginAuditLogsEntity {

    @Id
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private LoginAction action = LoginAction.LOGIN;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime timestamp;
}