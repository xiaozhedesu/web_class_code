package club.xiaozhe.shinycloud.service;

import club.xiaozhe.shinycloud.common.constant.ErrorCode;
import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.common.exception.BusinessException;
import club.xiaozhe.shinycloud.common.result.PageData;
import club.xiaozhe.shinycloud.dto.request.UpdateUserRequest;
import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.mapper.UserMapper;
import club.xiaozhe.shinycloud.util.PageConverter;
import club.xiaozhe.shinycloud.util.UserConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    /**
     * 检查用户是否为空，返回非空值
     *
     * @param user 用户对象
     * @return NonNull user
     * @throws BusinessException 当用户为空时
     */
    private static User requireUserNonNull(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public UserVO getUserById(Integer id) {
        return UserConverter.toVO(requireUserNonNull(userMapper.selectById(id)));
    }

    public UserVO getUserByUsername(String username) {
        return UserConverter.toVO(requireUserNonNull(userMapper.selectByUsername(username)));
    }

    /**
     * 分页查询用户，支持姓名模糊搜索
     * <p>
     * TODO(前端对接): 与旧 JPA 接口存在差异，切换前端时需同步调整：
     *   - 页码：本接口 page 从 1 开始，旧接口从 0 开始（前端原为 currentPage - 1）
     *   - 字段：返回 records/total，旧接口为 content/totalElements
     */
    public PageData<UserVO> list(int page, int size, String keyword) {
        IPage<User> selectedPage = userMapper.selectPage(
                new Page<>(page, size),
                Wrappers.<User>lambdaQuery()
                        .like(StringUtils.hasText(keyword), User::getRealName, keyword)
                        .orderByAsc(User::getId)
        );

        return PageConverter.from(selectedPage, UserConverter::toVO);
    }

    /**
     * 修改用户信息（真实姓名、电话）
     */
    @Transactional
    public UserVO update(Integer id, UpdateUserRequest request) {
        User user = requireUserNonNull(userMapper.selectById(id));

        boolean changed = false;
        if (request.realName() != null) {
            user.setRealName(request.realName());
            changed = true;
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
            changed = true;
        }

        if (changed) {
            userMapper.updateById(user);
        }
        return UserConverter.toVO(user);
    }

    /**
     * 删除用户
     */
    public void delete(Integer id) {
        userMapper.deleteById(id);
    }
}
