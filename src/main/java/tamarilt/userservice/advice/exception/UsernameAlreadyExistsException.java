package tamarilt.userservice.advice.exception;

/**
 * Исключение для случаев, когда пользователь с указанным именем уже существует в системе
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
