package club.xiaozhe.cloudservermanager.dto;

/**
 * 登录请求
 */
public record LoginRequest(
        String username,
        String password
) {}
