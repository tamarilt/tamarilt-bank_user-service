package tamarilt.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 24, message = "Имя пользователя должно быть от 3 до 24 символов")
    private String username;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 16, message = "Пароль должен быть от 6 до 16 символов")
    private String password;
}
