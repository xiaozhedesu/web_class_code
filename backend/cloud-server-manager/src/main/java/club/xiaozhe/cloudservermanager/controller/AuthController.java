package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.*;
import club.xiaozhe.cloudservermanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     *
     * @apiNote POST /api/auth/login
     */
    @PostMapping("/auth/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 用户注册
     *
      @apiNote POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    /**
     * 获取当前登录用户信息
     *
      @apiNote GET /api/user/me
     */
    @GetMapping("/user/me")
    public ApiResponse<UserResponse> currentUser() {
        return ApiResponse.success(authService.currentUser());
    }

    /**
     * 修改当前用户信息
     *
     * @apiNote PUT /api/user/me
     */
    @PutMapping("/user/me")
    public ApiResponse<UserResponse> updateProfile(@RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.success(authService.updateProfile(request));
    }

    /**
     * 用户登出操作
     * @apiNote POST /api/user/logout
     */
    @PostMapping("/user/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success(null);
    }
}
