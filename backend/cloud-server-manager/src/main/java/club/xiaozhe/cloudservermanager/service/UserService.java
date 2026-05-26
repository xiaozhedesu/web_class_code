package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.UpdateUserRequest;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 分页查询用户，支持姓名模糊搜索
     */
    public Page<User> listUsers(int page, int size, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(PageRequest.of(page, size));
        }
        return userRepository.findByRealNameContaining(keyword, PageRequest.of(page, size));
    }

    /**
     * 根据 ID 查找用户
     */
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * 更新用户信息（只更新 realName 和 phone）
     */
    @Transactional
    public void updateUser(User user, UpdateUserRequest request) {
        if (request.realName() != null) {
            user.setRealName(request.realName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        userRepository.save(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    /**
     * 判断用户是否存在
     */
    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }
}
