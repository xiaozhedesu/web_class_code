package club.xiaozhe.shinycloud.mapper;

import club.xiaozhe.shinycloud.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    default User selectByUsername(String username) {
        return selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

    @Select("SELECT EXISTS(SELECT 1 FROM t_user WHERE username = #{username})")
    boolean isExists(String username);
}
