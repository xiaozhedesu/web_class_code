package club.xiaozhe.shinycloud.controller;

import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.common.result.PageData;
import club.xiaozhe.shinycloud.common.result.Result;
import club.xiaozhe.shinycloud.dto.request.UpdateUserRequest;
import club.xiaozhe.shinycloud.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户，支持姓名模糊搜索
     * <p>
     * TODO(前端对接): 与旧 JPA 接口存在差异，切换前端时需同步调整：
     *   - page 由 0 基改为 1 基
     *   - 返回字段由 content/totalElements 改为 records/total
     */
    @GetMapping("/users")
    public Result<PageData<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int page,        // ← 改：0 → 1
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        return Result.success(userService.list(page, size, keyword));
    }

    /**
     * 修改用户信息（真实姓名、电话）
     */
    @PutMapping("/users/{id}")
    public Result<UserVO> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateUserRequest request) {
        return Result.success(userService.update(id, request));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success(null);
    }
}