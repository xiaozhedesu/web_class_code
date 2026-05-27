package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.LoginRequest;
import club.xiaozhe.cloudservermanager.dto.RegisterRequest;
import club.xiaozhe.cloudservermanager.dto.UserResponse;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setRole("USER");

        return ResponseEntity.ok(UserResponse.from(authService.register(user)));
    }

    /**
     * 获取当前登录用户信息（需 JWT 认证）
     * GET /api/user/me
     */
    @GetMapping("/user/me")
    public ResponseEntity<?> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(auth.getName());
        }
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
