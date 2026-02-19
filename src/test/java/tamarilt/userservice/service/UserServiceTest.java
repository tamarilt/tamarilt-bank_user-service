package tamarilt.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import tamarilt.userservice.advice.exception.InvalidCredentialsException;
import tamarilt.userservice.advice.exception.UserNotFoundException;
import tamarilt.userservice.advice.exception.UsernameAlreadyExistsException;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.entity.User;
import tamarilt.userservice.enums.Role;
import tamarilt.userservice.enums.Status;
import tamarilt.userservice.mapper.UserMapper;
import tamarilt.userservice.repository.UserRepository;
import tamarilt.userservice.service.Impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock 
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EventProducer eventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testRegisterSuccess() {
        RegistrationRequestDto request = new RegistrationRequestDto("testuser", "password123");
    
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    user.setRole(Role.USER);
    user.setStatus(Status.ACTIVE);

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(userMapper.toEntity(request)).thenReturn(user);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
    when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
    when(userMapper.toResponseDto(user, "accessToken", "refreshToken"))
        .thenReturn(new RegistrationResponseDto());

    RegistrationResponseDto response = userService.register(request);

    assertNotNull(response);
    verify(userRepository).save(any(User.class));

    }

    @Test
void testRegisterUsernameExists() {
    RegistrationRequestDto request = new RegistrationRequestDto("testuser", "password123");
    
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

    assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(request));
}

@Test
void testLoginSuccess() {
    LoginRequestDto request = new LoginRequestDto("testuser", "password123");
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    user.setPasswordHash("encodedPassword");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
    when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
    when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
    when(userMapper.toLoginResponseDto("accessToken", "refreshToken"))
        .thenReturn(new TokensResponseDto());

    TokensResponseDto response = userService.login(request);

    assertNotNull(response);
}

@Test
void testLoginUserNotFound() {
    LoginRequestDto request = new LoginRequestDto("testuser", "password123");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.login(request));
}

@Test
void testLoginInvalidPassword() {
    LoginRequestDto request = new LoginRequestDto("testuser", "wrongpassword");
    User user = new User();
    user.setPasswordHash("encodedPassword");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
}

@Test
void testRefreshTokenSuccess() {
    String token = "validToken";
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);

    when(jwtService.validateToken(token)).thenReturn(true);
    when(jwtService.getUserIdFromToken(token)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(jwtService.generateAccessToken(user)).thenReturn("newAccessToken");
    when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");

    TokensResponseDto response = userService.refreshToken(token);

    assertNotNull(response);
    assertEquals("newAccessToken", response.getAccessToken());
    assertEquals("newRefreshToken", response.getRefreshToken());
}

@Test
void testRefreshTokenInvalid() {
    String token = "invalidToken";

    when(jwtService.validateToken(token)).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> userService.refreshToken(token));
}

@Test
void testRefreshTokenUserNotFound() {
    String token = "validToken";
    UUID userId = UUID.randomUUID();

    when(jwtService.validateToken(token)).thenReturn(true);
    when(jwtService.getUserIdFromToken(token)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.refreshToken(token));
}
}
