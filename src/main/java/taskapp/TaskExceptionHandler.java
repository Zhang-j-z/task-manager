package taskapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice(assignableTypes = TaskController.class)
public class TaskExceptionHandler {
    @ExceptionHandler(InvalidTaskQueryException.class)
    public ResponseEntity<Map<String, String>> handleInvalidQuery(
            InvalidTaskQueryException exception) {
        return invalidParameter(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {
        return invalidParameter(exception.getName() + " 参数格式不正确");
    }

    private ResponseEntity<Map<String, String>> invalidParameter(String message) {
        return ResponseEntity.badRequest().body(
                Map.of("code", "INVALID_PARAMETER", "message", message));
    }
}
