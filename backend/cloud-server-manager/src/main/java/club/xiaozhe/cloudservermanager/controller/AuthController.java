package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
     * Body: { "username": "admin", "password": "123456" }
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 参数校验
        if (username == null || password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户名和密码不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        // 查用户
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(error);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(error);
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前登录用户信息（需 JWT 认证）
     * GET /api/user/me
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/user/me")
    public ResponseEntity<Map<String, Object>> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户不存在");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("role", user.getRole());
        result.put("createTime", user.getCreateTime());
        return ResponseEntity.ok(result);
    }
}
