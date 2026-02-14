package tamarilt.userservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tamarilt.userservice.enums.Role;
import tamarilt.userservice.enums.Status;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа после успешной регистрации пользователя")
public class RegistrationResponseDto {
    
    @Schema(description = "Уникальный идентификатор пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;
    
    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;
    
    @Schema(description = "Статус пользователя", example = "ACTIVE")
    private Status status;
    
    @Schema(description = "Дата и время создания пользователя", example = "2026-02-11T15:30:45.123")
    private LocalDateTime createdAt;
    
    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}
