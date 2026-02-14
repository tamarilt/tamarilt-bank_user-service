package tamarilt.userservice.service.Impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tamarilt.userservice.advice.exception.InvalidCredentialsException;
import tamarilt.userservice.advice.exception.UserNotFoundException;
import tamarilt.userservice.advice.exception.UsernameAlreadyExistsException;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;
import tamarilt.userservice.entity.User;
import tamarilt.userservice.enums.Role;
import tamarilt.userservice.enums.Status;
import tamarilt.userservice.mapper.UserMapper;
import tamarilt.userservice.repository.UserRepository;
import tamarilt.userservice.service.JwtService;
import tamarilt.userservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Override
    @Transactional
    public RegistrationResponseDto register(RegistrationRequestDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Юзернейм занят");
        }
        User user = userMapper.toEntity(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(encodedPassword);
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return userMapper.toResponseDto(savedUser, accessToken, refreshToken);
    }
    
    @Override
    public TokensResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Неверный пароль");
        }
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return userMapper.toLoginResponseDto(accessToken, refreshToken);
    }

    public TokensResponseDto refreshToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new InvalidCredentialsException("Невалидный refresh token");
        }

        UUID userId = jwtService.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return TokensResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
