package club.xiaozhe.cloudservermanager.dto;

/**
 * 注册请求
 */
public record RegisterRequest(
        String username,
        String password,
        String realName,
        String phone
) {}
