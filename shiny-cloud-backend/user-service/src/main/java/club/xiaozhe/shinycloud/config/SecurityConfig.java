package club.xiaozhe.shinycloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    /**
     * 密码的编解码器，通常使用BCrypt实现。
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 处理请求的调度员
     * ProviderManager会遍历其中的provider，让合适的provider处理请求
     *
     * @param uds     UserDetailsService
     * @param encoder BCryptPasswordEncoder
     * @return ProviderManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService uds,
            PasswordEncoder encoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    /**
     * 配置token编码器
     *
     * @param secret 密钥
     * @return NimbusJwtEncoder
     */
    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * SecurityFilterChain是Spring Security的配置入口
     * 我们在这里配置处理请求的行为，可以根据请求路径的不同进行不同的处理。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
