package club.xiaozhe.shinycloud.common.dto.response;

import club.xiaozhe.shinycloud.common.constant.UserRole;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户信息（不含密码）
 */
public record UserVO(
        Integer id,
        String username,
        String realName,
        String phone,
        UserRole role,
        LocalDateTime createTime
) {
}
