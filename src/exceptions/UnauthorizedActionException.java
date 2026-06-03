package exceptions;

public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String action) {
        super("Unauthorized action: " + action);
    }
}
