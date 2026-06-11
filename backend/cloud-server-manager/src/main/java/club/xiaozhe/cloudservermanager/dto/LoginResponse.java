package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.User;

/**
 * 登录响应
 */
public record LoginResponse(
        String token,
        String username,
        User.Role role
) {}
