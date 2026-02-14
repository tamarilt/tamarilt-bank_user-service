package tamarilt.userservice.service;

import java.util.UUID;

import tamarilt.userservice.entity.User;

/**
 * Сервис для работы с JWT токенами
 */
public interface JwtService {
    
    /**
     * Генерирует access token для пользователя
     *
     * @param user пользователь
     * @return JWT access token
     */
    String generateAccessToken(User user);
    
    /**
     * Генерирует refresh token для пользователя
     *
     * @param user пользователь
     * @return JWT refresh token
     */
    String generateRefreshToken(User user);
    
    /**
     * Валидирует JWT токен
     *
     * @param token JWT токен
     * @return true если токен валиден, false иначе
     */
    boolean validateToken(String token);
    
    /**
     * Извлекает ID пользователя из токена
     *
     * @param token JWT токен
     * @return UUID пользователя
     */
    UUID getUserIdFromToken(String token);
}
