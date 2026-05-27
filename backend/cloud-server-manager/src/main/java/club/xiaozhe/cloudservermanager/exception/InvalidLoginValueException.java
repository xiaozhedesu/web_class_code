package club.xiaozhe.cloudservermanager.exception;

/**
 * 登录参数不合法
 */
public class InvalidLoginValueException extends InvalidValueException {
    public InvalidLoginValueException() {
    }

    public InvalidLoginValueException(String message) {
        super(message);
    }

    public InvalidLoginValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLoginValueException(Throwable cause) {
        super(cause);
    }
}
