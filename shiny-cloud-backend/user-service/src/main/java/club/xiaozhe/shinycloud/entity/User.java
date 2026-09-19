package club.xiaozhe.shinycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private Role role;
    private LocalDateTime createTime;

    // FIXME 若被多个服务复用,考虑提取到 common 并改名 UserRole。
    /**
     * 用户存在两种身份，使用枚举表示
     */
    public enum Role {
        ADMIN, USER
    }
}
