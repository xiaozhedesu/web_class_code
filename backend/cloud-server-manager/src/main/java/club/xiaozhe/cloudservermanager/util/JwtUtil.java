package club.xiaozhe.cloudservermanager.util;

import club.xiaozhe.cloudservermanager.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${jwt.expiration}")
    private Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, StringRedisTemplate stringRedisTemplate) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成 token，携带用户名和角色
     *
     * @param username 用户名
     * @param role     角色
     * @return token
     */
    public String generateToken(String username, User.Role role) {
        Map<String, Object> claims = Map.of(
                "role", role.name()
        );

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();

        stringRedisTemplate.opsForValue().set("token:" + username, token, expiration, TimeUnit.MILLISECONDS);
        return token;

    }

    /**
     * 从 token 中获取用户名（不验证有效性，由 validateToken 负责）
     *
     * @param token token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从 token 中获取角色
     *
     * @param token token
     * @return role
     */
    public User.Role getRoleFromToken(String token) {
        String roleStr = getClaimFromToken(token, claims -> (String) claims.get("role"));
        return User.Role.valueOf(roleStr);
    }

    /**
     * 从 token 中获取过期时间
     *
     * @param token token
     * @return expiration
     */
    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 通用方法：从 token 中提取指定 claim
     *
     * @param token          token
     * @param claimsResolver Claims的getxxx方法
     * @param <T>            Claim值的类型
     * @return Claim
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 token 的所有 claims
     * （忽略过期异常，因为需要从过期 token 中也能提取用户名）
     *
     * @param token token
     * @return Claims
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // token 过期了但仍能提取 claims
            return e.getClaims();
        }
    }

    /**
     * 验证 token 是否有效
     *
     * @return true 表示 token 有效，false 表示无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            // 签名不匹配
            return false;
        } catch (MalformedJwtException e) {
            // token 格式错误
            return false;
        } catch (ExpiredJwtException e) {
            // token 过期
            return false;
        } catch (UnsupportedJwtException e) {
            // 不支持的 token
            return false;
        } catch (IllegalArgumentException e) {
            // token 为空
            return false;
        }
    }
}