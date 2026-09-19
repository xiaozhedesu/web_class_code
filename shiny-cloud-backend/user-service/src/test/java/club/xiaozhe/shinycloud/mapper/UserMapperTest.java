package club.xiaozhe.shinycloud.mapper;

import club.xiaozhe.shinycloud.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 连通性测试
     * id = 1 为默认admin账号，应当一直存在
     */
    @Test
    void connectionTest() {
        User user = User.builder()
                .username("test_user")
                .password("123456")
                .role(User.Role.USER)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(user);

        User selected = userMapper.selectById(user.getId());
        assertNotNull(selected);

        assertEquals(user.getUsername(), selected.getUsername());
    }
}
