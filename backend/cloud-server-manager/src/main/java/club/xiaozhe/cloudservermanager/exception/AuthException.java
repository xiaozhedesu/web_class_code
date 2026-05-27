package club.xiaozhe.cloudservermanager.exception;

/**
 * 用户登录、鉴权相关错误
 */
public class AuthException extends BusinessException {
    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}
