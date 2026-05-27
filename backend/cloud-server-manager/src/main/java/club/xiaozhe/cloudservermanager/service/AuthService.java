package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.LoginRequest;
import club.xiaozhe.cloudservermanager.dto.LoginResponse;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.exception.InvalidLoginValueException;
import club.xiaozhe.cloudservermanager.exception.InvalidValueException;
import club.xiaozhe.cloudservermanager.repository.UserRepository;
import club.xiaozhe.cloudservermanager.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository
            , PasswordEncoder passwordEncoder
            , JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidLoginValueException("用户名或密码错误！");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole());

    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidValueException("用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
