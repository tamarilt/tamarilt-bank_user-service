package tamarilt.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tamarilt.userservice.advice.exception.InvalidCredentialsException;
import tamarilt.userservice.advice.exception.UserNotFoundException;
import tamarilt.userservice.advice.exception.UsernameAlreadyExistsException;
import tamarilt.userservice.controller.Impl.UserControllerImpl;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;
import tamarilt.userservice.enums.Role;
import tamarilt.userservice.enums.Status;
import tamarilt.userservice.service.UserService;

@WebMvcTest(UserController.class)
@Import(UserControllerImpl.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegisterSuccess() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto("testuser", "password123");

         RegistrationResponseDto mockResponse = RegistrationResponseDto.builder()
            .id(UUID.randomUUID())
            .username(request.getUsername())
            .role(Role.USER)
            .status(Status.ACTIVE)
            .createdAt(LocalDateTime.now())
            .accessToken("mock-access-token")
            .refreshToken("mock-refresh-token")
            .build();

        when(userService.register(any(RegistrationRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value(request.getUsername()))
        .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
        .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    public void testRegisterUsernameExists() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto("testuser", "password123");
        when(userService.register(any(RegistrationRequestDto.class))).thenThrow(new UsernameAlreadyExistsException("Юзернейм занят"));

        mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Юзернейм занят"));
    }

    @Test
    public void testRegisterBadRequestUsernameShort() throws Exception{
         RegistrationRequestDto request = new RegistrationRequestDto("te", "password123");
         
         mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRegisterBadRequestUsernameLong() throws Exception{
         RegistrationRequestDto request = new RegistrationRequestDto("tetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetete", "password123");
         
         mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRegisterBadRequestPasswordShort() throws Exception {
    RegistrationRequestDto request = new RegistrationRequestDto("testuser", "12345");
    
    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRegisterBadRequestPasswordLong() throws Exception {
    RegistrationRequestDto request = new RegistrationRequestDto("testuser", "1234567891011121314");
    
    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidJson() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");

        TokensResponseDto mockResponse = TokensResponseDto.builder()
            .accessToken("mock-access-token")
            .refreshToken("mock-refresh-token")
            .build();

        when(userService.login(any(LoginRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");
        
        when(userService.login(any(LoginRequestDto.class)))
            .thenThrow(new UserNotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "wrongpassword");
        when(userService.login(any(LoginRequestDto.class))).thenThrow(new InvalidCredentialsException("Неверный пароль"));

        mockMvc.perform(post("/api/v1/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Неверный пароль"));
    }

    @Test
    public void testLoginBadRequestUsernameShort() throws Exception{
        LoginRequestDto request = new LoginRequestDto("te", "password123");
        
        mockMvc.perform(post("/api/v1/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testLoginBadRequestUsernameLong() throws Exception{
        LoginRequestDto request = new LoginRequestDto("tetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetetete", "password123");
        
        mockMvc.perform(post("/api/v1/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testLoginBadRequestPasswordShort() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "12345");
        
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testLoginBadRequestPasswordLong() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "1234567891011121314");
        
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testLoginInvalidJson() throws Exception {
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRefreshSuccess() throws Exception {
        String refreshToken = "valid-refresh-token";
        
        TokensResponseDto mockResponse = TokensResponseDto.builder()
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .build();
        
        when(userService.refreshToken(refreshToken)).thenReturn(mockResponse);
        
        mockMvc.perform(post("/api/v1/users/refresh")
                .contentType(MediaType.TEXT_PLAIN)
                .content(refreshToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    public void testRefreshInvalidToken() throws Exception {
        String refreshToken = "invalid-token";
        
        when(userService.refreshToken(refreshToken))
            .thenThrow(new InvalidCredentialsException("Невалидный refresh token"));
        
        mockMvc.perform(post("/api/v1/users/refresh")
                .contentType(MediaType.TEXT_PLAIN)
                .content(refreshToken))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Невалидный refresh token"));
    }

    @Test
    public void testRefreshUserNotFound() throws Exception {
        String refreshToken = "valid-token-but-user-deleted";
        
        when(userService.refreshToken(refreshToken))
            .thenThrow(new UserNotFoundException("Пользователь не найден"));
        
        mockMvc.perform(post("/api/v1/users/refresh")
                .contentType(MediaType.TEXT_PLAIN)
                .content(refreshToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    @Test
    public void testRefreshEmptyToken() throws Exception {
        mockMvc.perform(post("/api/v1/users/refresh")
                .contentType(MediaType.TEXT_PLAIN)
                .content(""))
            .andExpect(status().isBadRequest());
    }
    

}
