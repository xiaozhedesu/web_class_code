package club.xiaozhe.shinycloud.service;

import club.xiaozhe.shinycloud.common.constant.ErrorCode;
import club.xiaozhe.shinycloud.common.constant.UserRole;
import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.common.exception.BusinessException;
import club.xiaozhe.shinycloud.common.result.PageData;
import club.xiaozhe.shinycloud.dto.request.UpdateUserRequest;
import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

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
                .phone("15231223504")
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(testUser);
    }

    @Test
    void listTest() {
        PageData<UserVO> page = userService.list(1, 10, testUser.getRealName());

        assertNotEquals(0, page.total());
        UserVO selected = page.records().stream()
                .filter(vo -> Objects.equals(vo.id(), testUser.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("分页结果中未找到 testUser, id=" + testUser.getId()));
        assertEquals(testUser.getUsername(), selected.username());
    }

    @Test
    void updateTest() {
        UpdateUserRequest request = new UpdateUserRequest("我是陈千语", "13226877536");
        userService.update(testUser.getId(), request);

        UserVO selected = userService.getUserById(testUser.getId());
        assertEquals(request.realName(), selected.realName());
        assertEquals(request.phone(), selected.phone());
    }

    @Test
    void deleteTest() {
        userService.delete(testUser.getId());
        BusinessException e = assertThrows(BusinessException.class, () -> userService.getUserById(testUser.getId()));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getCode());
    }
}
