package club.xiaozhe.shinycloud.controller;

import club.xiaozhe.shinycloud.dto.*;
import club.xiaozhe.shinycloud.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @apiNote POST /api/auth/login
     */
    @PostMapping("/auth/login")
    public club.xiaozhe.common.result.Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return club.xiaozhe.common.result.Result.success(authService.login(request));
    }

    /**
     * 用户注册
     *
      @apiNote POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public club.xiaozhe.common.result.Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return club.xiaozhe.common.result.Result.success(authService.register(request));
    }

    /**
     * 获取当前登录用户信息
     *
      @apiNote GET /api/user/me
     */
    @GetMapping("/user/me")
    public club.xiaozhe.common.result.Result<UserResponse> currentUser() {
        return club.xiaozhe.common.result.Result.success(authService.currentUser());
    }

    /**
     * 修改当前用户信息
     *
     * @apiNote PUT /api/user/me
     */
    @PutMapping("/user/me")
    public club.xiaozhe.common.result.Result<UserResponse> updateProfile(@RequestBody @Valid UpdateUserRequest request) {
        return club.xiaozhe.common.result.Result.success(authService.updateProfile(request));
    }

    /**
     * 用户登出操作
     * @apiNote POST /api/user/logout
     */
    @PostMapping("/user/logout")
    public club.xiaozhe.common.result.Result<Void> logout() {
        authService.logout();
        return club.xiaozhe.common.result.Result.success(null);
    }
}
