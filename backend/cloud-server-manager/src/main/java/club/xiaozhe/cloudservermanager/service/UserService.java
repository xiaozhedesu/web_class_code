package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.UpdateUserRequest;
import club.xiaozhe.cloudservermanager.dto.UserPageResponse;
import club.xiaozhe.cloudservermanager.dto.UserResponse;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.ErrorCode;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据id获取用户信息
     *
     * @param id 用户id
     * @return 用户实体对象
     */
    public User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据id集合获取用户信息
     *
     * @param ids 用户id的集合
     * @return 用户列表
     */
    public List<User> findUserListById(Set<Integer> ids) {
        return userRepository.findAllById(ids);
    }

    /**
     * 根据username获取用户实体对象
     *
     * @param username 用户名
     * @return 用户实体对象
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 判断是否存在名为username的用户
     *
     * @param username 用户名
     * @return 存在返回true
     */
    public boolean isUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 保存用户，并返回保存后的用户实体对象
     * @param user User对象
     * @return 用户实体对象
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 分页查询用户，支持姓名模糊搜索
     */
    public UserPageResponse listUsers(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<User> userPage;
        if (keyword == null || keyword.isEmpty()) {
            userPage = userRepository.findAll(pageRequest);
        } else {
            userPage = userRepository.findByRealNameContaining(keyword, pageRequest);
        }

        return UserPageResponse.from(userPage);
    }

    /**
     * 更新用户信息（只更新 realName 和 phone）
     */
    @Transactional
    public UserResponse updateUser(Integer id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (request.realName() != null) {
            user.setRealName(request.realName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * 删除用户
     */
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        userRepository.deleteById(id);
    }
}
