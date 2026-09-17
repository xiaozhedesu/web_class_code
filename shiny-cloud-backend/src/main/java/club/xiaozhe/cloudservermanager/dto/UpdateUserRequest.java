package club.xiaozhe.cloudservermanager.dto;

import jakarta.validation.constraints.Pattern;

/**
 * 修改用户请求
 */
public record UpdateUserRequest(
        String realName,
        @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码不是一个合法的中国手机号码")
        String phone
) {}
