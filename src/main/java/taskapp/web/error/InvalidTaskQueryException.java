package taskapp.web.error;

public class InvalidTaskQueryException extends RuntimeException {
    public InvalidTaskQueryException(String message) {
        super(message);
    }
}
