package club.xiaozhe.cloudservermanager.exception;

/**
 * 参数不合法错误，并入业务错误的子错误
 */
public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidValueException(Throwable cause) {
        super(cause);
    }
}
