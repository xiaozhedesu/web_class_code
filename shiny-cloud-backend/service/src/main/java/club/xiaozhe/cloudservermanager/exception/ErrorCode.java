package club.xiaozhe.cloudservermanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 使用错误码枚举统一管理所有业务错误信息
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* ----- NOT FOUND ----- */
    USER_NOT_FOUND(1001, "用户不存在！", HttpStatus.NOT_FOUND),
    SERVER_NOT_FOUND(1002, "服务器套餐不存在！", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(1003, "订单不存在！", HttpStatus.NOT_FOUND),

    /* ----- OTHER ----- */
    UNAUTHORIZED(1004, "未授权的操作: %s", HttpStatus.UNAUTHORIZED),
    INVALID_VALUE(1005, "发现数据错误: %s", HttpStatus.BAD_REQUEST),
    USERNAME_OR_PASSWORD_ERROR(1006, "用户名或密码错误！", HttpStatus.UNAUTHORIZED),
    STATUS_UNDEFINED(1007, "状态码无效: %s", HttpStatus.BAD_REQUEST),

    /* ----- UNKNOWN ----- */
    UNKNOWN_ERROR(1999, "发生未知错误", HttpStatus.INTERNAL_SERVER_ERROR);

    /* ----- Define ----- */
    private final int code;
    private final String message;
    private final HttpStatus status;
}
