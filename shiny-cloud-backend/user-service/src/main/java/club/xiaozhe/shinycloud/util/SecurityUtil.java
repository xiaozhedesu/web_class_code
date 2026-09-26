package club.xiaozhe.shinycloud.util;

import club.xiaozhe.shinycloud.common.constant.ErrorCode;
import club.xiaozhe.shinycloud.common.exception.BusinessException;
import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserMapper userMapper;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未获取到登录信息");
        }

        User user = userMapper.selectByUsername(auth.getName());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }

}
