package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.LoginRequest;
import club.xiaozhe.cloudservermanager.dto.LoginResponse;
import club.xiaozhe.cloudservermanager.dto.RegisterRequest;
import club.xiaozhe.cloudservermanager.dto.UserResponse;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.InvalidLoginValueException;
import club.xiaozhe.cloudservermanager.exception.InvalidValueException;
import club.xiaozhe.cloudservermanager.exception.UserNotFoundException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new InvalidLoginValueException("用户名和密码不能为空！");
        }

        User user = userRepository.findByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidLoginValueException("用户名或密码错误！");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
    }

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new InvalidValueException("用户名和密码不能为空！");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new InvalidValueException("用户名已存在！");
        }

        final String username = request.username();
        if (username.length() < 6 || username.length() > 30) {
            throw new InvalidValueException("用户名长度限制为6~30！");
        }
        if (!username.matches("[a-zA-Z]+")) {
            throw new InvalidValueException("用户名只允许使用英文字母！");
        }

        final String password = request.password();
        if (password.length() < 6 || password.length() > 64) {
            throw new InvalidValueException("密码长度限制为6~64！");
        }
        if (!password.matches("[a-zA-Z0-9_]+")) {
            throw new InvalidValueException("密码只允许为数字、字母和下划线（_）组合！");
        }

        final String phone = request.phone();
        if (!phone.matches("^1[3456789]\\d{9}$")) {
            throw new InvalidValueException("手机号码不是一个合法的中国手机号码！");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(request.realName());
        user.setPhone(phone);
        user.setRole("USER");
        userRepository.save(user);

        return ResponseEntity.ok(UserResponse.from(user));
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
