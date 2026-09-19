package club.xiaozhe.shinycloud.mapper;

import club.xiaozhe.shinycloud.common.constant.UserRole;
import club.xiaozhe.shinycloud.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    void createTestUserAccount() {
        testUser = User.builder()
                .username("test_user")
                .password("123456")
                .realName("测试用户")
                .role(UserRole.USER)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(testUser);
    }

    @Test
    void selectTestUserByIdTest() {
        User selected = userMapper.selectById(testUser.getId());
        assertNotNull(selected);

        assertEquals(testUser.getUsername(), selected.getUsername());
    }

    @Test
    void selectTestUserByUsernameTest() {
        User selected = userMapper.selectByUsername(testUser.getUsername());
        assertNotNull(selected);

        assertEquals(testUser.getUsername(), selected.getUsername());
    }

    @Test
    void testUserShouldUniqueTest() {
        User user = User.builder()
                .username(testUser.getUsername())
                .password("123456")
                .role(UserRole.USER)
                .createTime(LocalDateTime.now())
                .build();

        assertThrows(DuplicateKeyException.class, () -> userMapper.insert(user));
    }
}
