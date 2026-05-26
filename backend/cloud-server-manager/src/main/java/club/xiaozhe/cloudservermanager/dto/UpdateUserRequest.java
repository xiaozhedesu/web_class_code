package club.xiaozhe.cloudservermanager.dto;

/**
 * 修改用户请求
 */
public record UpdateUserRequest(
        String realName,
        String phone
) {}
