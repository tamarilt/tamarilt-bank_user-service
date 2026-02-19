package tamarilt.userservice.controller;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tamarilt.userservice.advice.dto.ErrorResponseDto;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokenValidationResponseDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;

public interface UserController {

    @Operation(summary = "Регистрация пользователя", description = "Создание нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован", content = @Content(schema = @Schema(implementation = RegistrationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные запроса", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким username уже существует", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<RegistrationResponseDto> register(RegistrationRequestDto request);

    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = @Content(schema = @Schema(implementation = TokensResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные запроса", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<TokensResponseDto> login(LoginRequestDto request);

    @Operation(summary = "Обновление токенов", description = "Получение новых access и refresh токенов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены", content = @Content(schema = @Schema(implementation = TokensResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Невалидный refresh token", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<TokensResponseDto> refresh(String request);

    @Operation(summary = "Валидация токена", description = "Проверяет валидность access token и возвращает userId и role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен валиден", content = @Content(schema = @Schema(implementation = TokenValidationResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Невалидный токен", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<TokenValidationResponseDto> validateToken(String token);
}
