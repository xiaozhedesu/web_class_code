package club.xiaozhe.cloudservermanager.config;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import club.xiaozhe.cloudservermanager.exception.AuthException;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.InvalidLoginValueException;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ApiResponse<Void> handleAuthException(AuthException e) {
        return ApiResponse.error(400, "发生用户鉴权错误：" + e.getMessage());
    }

    @ExceptionHandler(InvalidLoginValueException.class)
    public ApiResponse<Void> handleInvalidLoginValueException(InvalidLoginValueException e) {
        return ApiResponse.error(401, "登录发生错误：" + e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<Void> handleUserNotFoundException(UserNotFoundException e) {
        return ApiResponse.error(404, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.error(400, e.getMessage());
    }
}
