package club.xiaozhe.cloudservermanager.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成 token，携带用户名和角色
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 从 token 中获取用户名（不验证有效性，由 validateToken 负责）
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 从 token 中获取角色
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> (String) claims.get("role"));
    }

    // 从 token 中获取过期时间
    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 通用方法：从 token 中提取指定 claim
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 解析 token 的所有 claims（忽略过期异常，因为需要从过期 token 中也能提取用户名）
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // token 过期了但仍能提取 claims
            return e.getClaims();
        }
    }

    /**
     * 验证 token 是否有效
     * @return true 表示 token 有效，false 表示无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
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