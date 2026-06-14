package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.*;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.ErrorCode;
import club.xiaozhe.cloudservermanager.util.JwtUtil;
import club.xiaozhe.cloudservermanager.util.SecurityUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    /**
     * 使用用户相关的数据库能力
     */
    private final UserService userService;
    /**
     * 给密码加密
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * 生成token
     */
    private final JwtUtil jwtUtil;
    /**
     * 获取用户信息
     */
    private final SecurityUtil securityUtil;
    /**
     * 登录用
     */
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            SecurityUtil securityUtil,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.securityUtil = securityUtil;
        this.authenticationManager = authenticationManager;
    }

    /* ----- apis ----- */

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authenticated = authenticationManager.authenticate(authToken);

        String name = authenticated.getName();
        User.Role role = User.Role.valueOf(authenticated.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElse("USER"));
        String token = jwtUtil.generateToken(name, role);
        return new LoginResponse(token, name, role);
    }

    /**
     * 用户注册
     */
    public UserResponse register(RegisterRequest request) {
        // 组装User
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setRole(User.Role.USER);

        if (userService.isUserExists(request.username())) {
            throw new BusinessException(ErrorCode.INVALID_VALUE, String.format("用户名 %s 已存在", user.getUsername()));
        }

        // 进行密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 返回到前端的数据不能含密码
        return UserResponse.from(userService.save(user));
    }

    /**
     * 获取当前登录用户信息
     */
    public UserResponse currentUser() {
        // 返回到前端的数据不能含密码
        return UserResponse.from(securityUtil.getCurrentUser());
    }

    /**
     * 修改当前用户信息
     */
    public UserResponse updateProfile(UpdateUserRequest request) {
        // 获取
        User user = securityUtil.getCurrentUser();

        // 修改
        if (request.realName() != null) user.setRealName(request.realName());
        if (request.phone() != null) user.setPhone(request.phone());

        // 保存
        return UserResponse.from(userService.save(user));
    }
}
