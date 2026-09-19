package club.xiaozhe.shinycloud.mapper;

import club.xiaozhe.shinycloud.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public interface UserMapper extends BaseMapper<User> {
    default User selectByUsername(String username) {
        return selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
    }
}
