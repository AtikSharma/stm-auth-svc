package com.taskmanager.auth.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refresh_tokens")
public class RefreshTokensEntity {

    @Id
    private String id;

    @NotNull
    @Indexed
    private String userId;

    @NotBlank
    private String token;

    @NotNull
    private LocalDateTime expiryDateTime;

    private LocalDateTime createdDateTime;

    @NotNull
    private Boolean isRevoked;

    private String previousJti;

    @NotNull
    private Integer refreshCount;
}
