package tamarilt.userservice.service;

import tamarilt.userservice.advice.exception.InvalidCredentialsException;
import tamarilt.userservice.advice.exception.UserNotFoundException;
import tamarilt.userservice.advice.exception.UsernameAlreadyExistsException;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;

public interface UserService {
    
    /**
     * Регистрирует нового пользователя в системе
     * 
     * @param request DTO с данными для регистрации (username, password)
     * @return DTO с информацией о зарегистрированном пользователе
     * @throws UsernameAlreadyExistsException если username уже занят
     */
    RegistrationResponseDto register(RegistrationRequestDto request);
    
    /**
     * Аутентифицирует пользователя в системе
     * 
     * @param request DTO с данными для входа (username, password)
     * @return DTO с токенами доступа
     * @throws UserNotFoundException если пользователь не найден
     * @throws InvalidCredentialsException если пароль неверный
     */
    TokensResponseDto login(LoginRequestDto request);

    /**
     * Обновляет access и refresh токены
     * 
     * @param token refresh token
     * @return DTO с новыми токенами
     * @throws InvalidCredentialsException если refresh token невалиден
     * @throws UserNotFoundException если пользователь не найден
     */
    TokensResponseDto refreshToken(String token);
}
