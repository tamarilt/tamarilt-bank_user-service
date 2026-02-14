package tamarilt.userservice.advice.exception;

/**
 * Исключение для случаев, когда пользователь не найден в системе
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
