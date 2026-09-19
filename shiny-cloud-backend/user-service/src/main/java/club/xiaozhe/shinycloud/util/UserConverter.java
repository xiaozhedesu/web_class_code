package club.xiaozhe.shinycloud.util;

import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.entity.User;

public final class UserConverter {
    private UserConverter() {
    }

    /**
     * 将用户实体类转换为视图类
     *
     * @param user 用户实体类
     * @return UserVO
     */
    public static UserVO toVO(User user) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getRole(),
                user.getCreateTime()
        );
    }
}
