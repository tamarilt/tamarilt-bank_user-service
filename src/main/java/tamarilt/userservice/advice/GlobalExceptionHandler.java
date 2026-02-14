package tamarilt.userservice.advice;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import tamarilt.userservice.advice.dto.ErrorResponseDto;
import tamarilt.userservice.advice.exception.InvalidCredentialsException;
import tamarilt.userservice.advice.exception.UserNotFoundException;
import tamarilt.userservice.advice.exception.UsernameAlreadyExistsException;

public interface GlobalExceptionHandler {
    
    @Operation(
        summary = "Обработка ошибок валидации (@Valid)",
        description = "Возникает при невалидных данных в теле запроса",
        responses = {
            @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации полей",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex);
    
    @Operation(
        summary = "Обработка нарушений ограничений (Bean Validation)",
        description = "Возникает при нарушении ограничений валидации",
        responses = {
            @ApiResponse(
                responseCode = "400",
                description = "Нарушение ограничений валидации",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex);
    
    @Operation(
        summary = "Обработка несоответствия типов аргументов",
        description = "Возникает при передаче параметра неверного типа",
        responses = {
            @ApiResponse(
                responseCode = "400",
                description = "Некорректный тип параметра",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex);
    
    @Operation(
        summary = "Обработка отсутствующих параметров запроса",
        description = "Возникает когда обязательный параметр запроса отсутствует",
        responses = {
            @ApiResponse(
                responseCode = "400",
                description = "Отсутствует обязательный параметр",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleMissingParameter(MissingServletRequestParameterException ex);
    
    @Operation(
        summary = "Обработка невалидного формата тела запроса",
        description = "Возникает при невалидном JSON или теле запроса",
        responses = {
            @ApiResponse(
                responseCode = "400",
                description = "Невалидный формат тела запроса",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleInvalidJson(HttpMessageNotReadableException ex);
    
    @Operation(
        summary = "Обработка ненайденного endpoint",
        description = "Возникает когда запрашиваемый endpoint не существует",
        responses = {
            @ApiResponse(
                responseCode = "404",
                description = "Endpoint не найден",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleNotFound(NoHandlerFoundException ex);
    
    @Operation(
        summary = "Обработка неподдерживаемого HTTP метода",
        description = "Возникает когда используется неподдерживаемый HTTP метод для endpoint",
        responses = {
            @ApiResponse(
                responseCode = "405",
                description = "HTTP метод не поддерживается",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex);
    
    @Operation(
        summary = "Обработка конфликта при регистрации пользователя",
        description = "Возникает когда username уже существует",
        responses = {
            @ApiResponse(
                responseCode = "409",
                description = "Пользователь с таким username уже существует",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex);
    
    @Operation(
        summary = "Обработка ошибки аутентификации",
        description = "Возникает при неверных учетных данных",
        responses = {
            @ApiResponse(
                responseCode = "401",
                description = "Неверные учетные данные",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleInvalidCredentialsException(InvalidCredentialsException ex);
    
    @Operation(
        summary = "Обработка отсутствия пользователя",
        description = "Возникает когда пользователь не найден",
        responses = {
            @ApiResponse(
                responseCode = "404",
                description = "Пользователь не найден",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex);
    
    @Operation(
        summary = "Обработка всех необработанных исключений",
        description = "Глобальный обработчик для всех остальных исключений",
        responses = {
            @ApiResponse(
                responseCode = "500",
                description = "Внутренняя ошибка сервера",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex);
}
