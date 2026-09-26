package club.xiaozhe.shinycloud.service;

import club.xiaozhe.shinycloud.common.constant.UserRole;
import club.xiaozhe.shinycloud.dto.request.LoginRequest;
import club.xiaozhe.shinycloud.dto.request.RegisterRequest;
import club.xiaozhe.shinycloud.dto.response.LoginResponse;
import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final String testPassword = "123456";

    @BeforeEach
    void createTestUserAccount() {
        testUser = User.builder()
                .username("test_user")
                .password(passwordEncoder.encode(testPassword))
                .realName("测试用户")
                .role(UserRole.USER)
                .phone("15231223504")
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(testUser);
    }

    @Test
    void loginTest() {
        LoginRequest request = new LoginRequest(testUser.getUsername(), testPassword);
        LoginResponse response = authService.login(request);

        assertNotNull(response.token());
        assertEquals(testUser.getRole(), response.role());
        assertEquals(testUser.getUsername(), response.username());
    }

    @Test
    void registerTest() {
        RegisterRequest registerRequest = new RegisterRequest("test_user_" + UUID.randomUUID(), testPassword, null, null);
        authService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest(registerRequest.username(), testPassword);
        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response.token());
        assertEquals(UserRole.USER, response.role());
        assertEquals(registerRequest.username(), response.username());
    }
}
