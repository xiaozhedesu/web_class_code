package club.xiaozhe.cloudservermanager.config;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 请求体 JSON 字段名不匹配时，返回友好的中文提示
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        // 递归遍历 cause 链，找到 UnrecognizedPropertyException
        UnrecognizedPropertyException upe = findUnrecognizedPropertyException(ex);

        if (upe != null) {
            String unknownField = upe.getPropertyName();
            String knownFields = upe.getKnownPropertyIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("、"));
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "未知字段 \"" + unknownField + "\"，支持的字段：" + knownFields
            ));
        }

        // 调试：打印异常链，看看实际是什么类型
        log.warn("JSON 解析失败，异常链:", ex);
        return ResponseEntity.badRequest().body(Map.of(
                "message", "请求体格式错误，请检查 JSON 格式"
        ));
    }

    /**
     * 递归查找 UnrecognizedPropertyException
     */
    private UnrecognizedPropertyException findUnrecognizedPropertyException(Throwable t) {
        if (t == null) return null;
        if (t instanceof UnrecognizedPropertyException upe) return upe;
        return findUnrecognizedPropertyException(t.getCause());
    }

    /**
     * 处理字段不合法错误，返回友好提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String firstErrorMessage = e.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");

        return ResponseEntity.badRequest()
                .body(Map.of("message", firstErrorMessage));
    }
}
