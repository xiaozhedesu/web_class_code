package club.xiaozhe.cloudservermanager.exception;

import lombok.Getter;

/**
 * 业务逻辑错误
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode code;

    /**
     * 根据错误码创建业务异常
     * @param code 错误码枚举
     * @param args 错误信息格式化参数
     */
    public BusinessException(ErrorCode code, Object... args) {
        super(String.format(code.getMessage(), args));
        this.code = code;
    }
}
