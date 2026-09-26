package club.xiaozhe.shinycloud.service;

import club.xiaozhe.shinycloud.common.constant.ErrorCode;
import club.xiaozhe.shinycloud.common.constant.UserRole;
import club.xiaozhe.shinycloud.common.dto.response.UserVO;
import club.xiaozhe.shinycloud.common.exception.BusinessException;
import club.xiaozhe.shinycloud.dto.request.LoginRequest;
import club.xiaozhe.shinycloud.dto.request.RegisterRequest;
import club.xiaozhe.shinycloud.dto.response.LoginResponse;
import club.xiaozhe.shinycloud.entity.User;
import club.xiaozhe.shinycloud.mapper.UserMapper;
import club.xiaozhe.shinycloud.util.SecurityUtil;
import club.xiaozhe.shinycloud.util.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate stringRedisTemplate;
    private final SecurityUtil securityUtil;

    @Value("${jwt.expiration}")
    private Duration expiration;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        Authentication authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails user = (UserDetails) authenticated.getPrincipal();
        Objects.requireNonNull(user, "principal should not be null");
        
        UserRole role = UserRole.valueOf(
                authenticated.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(StringUtils::hasText)
                        .findFirst()
                        .map(s -> s.substring(5))
                        .orElse("USER")
        );

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .claim("role", role)
                .issuedAt(now)
                .expiresAt(now.plus(expiration))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        stringRedisTemplate.opsForValue().set("token:" + user.getUsername(), token, expiration);
        return new LoginResponse(token, user.getUsername(), role);
    }

    /**
     * 用户注册
     */
    public UserVO register(RegisterRequest request) {
        if (userMapper.isExists(request.username())) {
            throw new BusinessException(ErrorCode.INVALID_VALUE, String.format("用户名 %s 已存在", request.username()));
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .realName(request.realName())
                .phone(request.phone())
                .role(UserRole.USER)
                .build();

        userMapper.insert(user);
        return UserConverter.toVO(user);
    }

    /**
     * 登出用户
     */
    public void logout() {
        User user = securityUtil.getCurrentUser();

        stringRedisTemplate.delete("token:" + user.getUsername());
        SecurityContextHolder.clearContext();
    }
}
