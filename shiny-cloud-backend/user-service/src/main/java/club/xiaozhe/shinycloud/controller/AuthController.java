package club.xiaozhe.shinycloud.controller;

import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.common.result.Result;
import club.xiaozhe.shinycloud.dto.request.LoginRequest;
import club.xiaozhe.shinycloud.dto.request.RegisterRequest;
import club.xiaozhe.shinycloud.dto.response.LoginResponse;
import club.xiaozhe.shinycloud.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/auth/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/user/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
