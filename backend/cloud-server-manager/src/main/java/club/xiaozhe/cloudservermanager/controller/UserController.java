package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import club.xiaozhe.cloudservermanager.dto.UpdateUserRequest;
import club.xiaozhe.cloudservermanager.dto.UserPageResponse;
import club.xiaozhe.cloudservermanager.dto.UserResponse;
import club.xiaozhe.cloudservermanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户，支持姓名模糊搜索
     *
     * @apiNote GET /api/admin/users?page=0&size=10&keyword=张三
     */
    @GetMapping("/users")
    public ApiResponse<UserPageResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        return ApiResponse.success(userService.listUsers(page, size, keyword));
    }

    /**
     * 修改用户信息（真实姓名、电话）
     *
     * @apiNote PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }

    /**
     * 删除用户
     *
     * @apiNote DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    /**
     * 捕获无 ID 的删除请求，返回友好提示
     */
    @DeleteMapping({"/users", "/users/"})
    public ResponseEntity<ApiResponse<Void>> deleteUserWithoutId() {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "缺少用户 ID"));
    }
}
