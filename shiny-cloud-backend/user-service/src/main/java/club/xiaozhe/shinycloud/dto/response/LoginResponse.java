package club.xiaozhe.shinycloud.dto.response;

import club.xiaozhe.shinycloud.common.constant.UserRole;

/**
 * 登录响应
 */
public record LoginResponse(
        String token,
        String username,
        UserRole role
) {
}
