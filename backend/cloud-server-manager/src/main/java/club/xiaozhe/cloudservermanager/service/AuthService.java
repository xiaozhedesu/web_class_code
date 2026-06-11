package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.*;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.*;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    /**
     * JPA提供数据库操作服务
     */
    private final UserRepository userRepository;
    /**
     * 给密码加密
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * 生成token
     */
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository
            , PasswordEncoder passwordEncoder
            , JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 进行登录操作
     *
     * @param request 登录请求体
     * @return 登录返回体（token、username、role）
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    /**
     * 进行注册操作
     *
     * @param request 注册请求体
     * @return 用户返回体
     */
    public UserResponse register(RegisterRequest request) {
        // 组装User
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setRole(User.Role.USER);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_VALUE, String.format("用户名 %s 已存在", user.getUsername()));
        }

        // 进行密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 返回到前端的数据不能含密码
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户返回体
     */
    public UserResponse currentUser() {
        // 返回到前端的数据不能含密码
        return UserResponse.from(getUserInfo());
    }

    /**
     * 修改用户信息
     *
     * @param request 修改请求体
     * @return 用户返回体
     */
    public UserResponse updateProfile(UpdateUserRequest request) {
        // 获取
        User user = getUserInfo();

        // 修改
        if (request.realName() != null) user.setRealName(request.realName());
        if (request.phone() != null) user.setPhone(request.phone());

        // 保存
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    private User getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未获取到登录信息");
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
