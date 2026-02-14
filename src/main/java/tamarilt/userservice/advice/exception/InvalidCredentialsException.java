package tamarilt.userservice.advice.exception;

/**
 * Исключение для случаев, когда неверные креды
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
