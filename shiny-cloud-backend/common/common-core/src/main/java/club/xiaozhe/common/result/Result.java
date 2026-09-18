package club.xiaozhe.common.result;

/**
 * 通用返回结构
 *
 * @param code    业务码
 * @param message 信息（成功或失败）
 * @param data    数据（可为null）
 * @param <T>     数据类型
 */
public record Result<T>(
        int code,
        String message,
        T data
) {
    /**
     * 带返回值的成功回复
     *
     * @param data 数据
     * @param <T>  返回值类型
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 不带返回值的成功回复
     */
    public static Result<Void> success() {
        return success(null);
    }

    /**
     * 错误回复，指定状态码和错误信息
     *
     * @param code    状态码
     * @param message 错误信息
     */
    public static Result<Void> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}