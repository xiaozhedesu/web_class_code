package club.xiaozhe.shinycloud.util;

import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.common.exception.BusinessException;
import club.xiaozhe.shinycloud.common.constant.ErrorCode;
import club.xiaozhe.shinycloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;

    /**
     * 获取当前用户信息，如果为空则抛出业务异常。
     *
     * @return 用户信息
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName()))
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未获取到登录信息");
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
