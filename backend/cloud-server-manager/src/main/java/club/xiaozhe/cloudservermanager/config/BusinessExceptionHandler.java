package club.xiaozhe.cloudservermanager.config;

import club.xiaozhe.cloudservermanager.exception.AuthException;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.InvalidLoginValueException;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "发生用户鉴权错误：" + e.getMessage()));
    }

    @ExceptionHandler(InvalidLoginValueException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLoginValueException(InvalidLoginValueException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "登录发生错误：" + e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }
}
