package club.xiaozhe.cloudservermanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 注册请求
 */
public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 6, max = 30, message = "用户名长度限制为6~30")
        @Pattern(regexp = "[a-zA-Z]+", message = "用户名只允许使用英文字母")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 64, message = "密码长度限制为6~64")
        @Pattern(regexp = "[a-zA-Z0-9_]+", message = "密码只允许为数字、字母和下划线（_）组合")
        String password,

        String realName,

        @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码不是一个合法的中国手机号码")
        String phone
) {
}
