package club.xiaozhe.shinycloud.dto;

import club.xiaozhe.shinycloud.entity.User;

/**
 * 登录响应
 */
public record LoginResponse(
        String token,
        String username,
        User.Role role
) {}
