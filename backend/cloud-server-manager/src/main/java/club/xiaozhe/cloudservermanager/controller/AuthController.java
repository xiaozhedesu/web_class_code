package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.*;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/auth/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setRole(User.USER);

        return ApiResponse.success(UserResponse.from(authService.register(user)));
    }

    /**
     * 获取当前登录用户信息（需 JWT 认证）
     * GET /api/user/me
     */
    @GetMapping("/user/me")
    public ApiResponse<UserResponse> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(auth.getName());
        }
        return ApiResponse.success(UserResponse.from(user));
    }

    /**
     * 修改当前用户信息
     * PUT /api/user/me
     */
    @PutMapping("/user/me")
    public ApiResponse<UserResponse> updateProfile(@RequestBody @Valid UpdateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (request.realName() != null) user.setRealName(request.realName());
        if (request.phone() != null) user.setPhone(request.phone());
        userRepository.save(user);

        return ApiResponse.success(UserResponse.from(user));
    }
}
