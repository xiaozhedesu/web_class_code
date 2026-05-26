package club.xiaozhe.cloudservermanager.dto;

import club.xiaozhe.cloudservermanager.entity.User;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户信息（不含密码）
 */
public record UserResponse(
        Integer id,
        String username,
        String realName,
        String phone,
        String role,
        LocalDateTime createTime
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getRole(),
                user.getCreateTime()
        );
    }
}
