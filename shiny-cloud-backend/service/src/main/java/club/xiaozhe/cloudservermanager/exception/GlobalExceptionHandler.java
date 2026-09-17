package club.xiaozhe.cloudservermanager.exception;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 请求体 JSON 字段名不匹配时，返回友好的中文提示
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParseError(HttpMessageNotReadableException ex) {
        UnrecognizedPropertyException upe = findUnrecognizedPropertyException(ex);

        if (upe != null) {
            String unknownField = upe.getPropertyName();
            String knownFields = upe.getKnownPropertyIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("、"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "未知字段 \"" + unknownField + "\"，支持的字段：" + knownFields));
        }

        log.warn("JSON 解析失败，异常链:", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "请求体格式错误，请检查 JSON 格式"));
    }

    /**
     * 迭代查找 UnrecognizedPropertyException
     */
    private UnrecognizedPropertyException findUnrecognizedPropertyException(Throwable t) {
        if (t == null) return null;
        Throwable curr = t;
        while (curr != null) {
            if (curr instanceof UnrecognizedPropertyException upe) return upe;
            curr = curr.getCause();
        }
        return null;
    }

    /**
     * 处理字段不合法错误，返回友好提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String firstErrorMessage = e.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");

        ErrorCode code = ErrorCode.INVALID_VALUE;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code.getCode(), firstErrorMessage));
    }

    /**
     * 处理业务错误的处理类
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getCode();
        log.warn("发生业务异常：code = {}, message = {}", e.getCode(), e.getMessage());
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code.getCode(), e.getMessage()));
    }

    /**
     * 处理运行时异常的处理类
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException re) {
        log.error("发生未知错误", re);
        ErrorCode code = ErrorCode.UNKNOWN_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code.getCode(), code.getMessage()));
    }
}
