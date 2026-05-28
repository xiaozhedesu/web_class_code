package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.UpdateUserRequest;
import club.xiaozhe.cloudservermanager.dto.UserResponse;
import club.xiaozhe.cloudservermanager.entity.User;
import club.xiaozhe.cloudservermanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户，支持姓名模糊搜索
     * GET /api/admin/users?page=0&size=10&keyword=张三
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<User> userPage = userService.listUsers(page, size, keyword);

        return ResponseEntity.ok(Map.of(
                "content", userPage.getContent().stream().map(UserResponse::from).toList(),
                "totalElements", userPage.getTotalElements(),
                "totalPages", userPage.getTotalPages(),
                "currentPage", userPage.getNumber(),
                "size", userPage.getSize()
        ));
    }

    /**
     * 修改用户信息（真实姓名、电话）
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateUserRequest request) {

        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "用户不存在"));
        }

        userService.updateUser(user, request);
        return ResponseEntity.ok(UserResponse.from(user));
    }


    /**
     * 删除用户
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "用户不存在"));
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }

    /**
     * 捕获无 ID 的删除请求，返回友好提示
     */
    @DeleteMapping({"/users", "/users/"})
    public ResponseEntity<Map<String, Object>> deleteUserWithoutId() {
        return ResponseEntity.badRequest().body(Map.of("message", "缺少用户 ID"));
    }
}
