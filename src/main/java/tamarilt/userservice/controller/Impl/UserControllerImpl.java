package tamarilt.userservice.controller.Impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tamarilt.userservice.controller.UserController;
import tamarilt.userservice.dto.request.LoginRequestDto;
import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;
import tamarilt.userservice.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    
    private final UserService userService;
    
    @Override
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) {
        RegistrationResponseDto response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    @PostMapping("/login")
    public ResponseEntity<TokensResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        TokensResponseDto response = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokensResponseDto> refresh(@RequestBody String token) {
        TokensResponseDto response = userService.refreshToken(token);
        return ResponseEntity.ok(response);
    }


}
