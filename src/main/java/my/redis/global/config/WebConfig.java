package my.redis.global.config;

import lombok.RequiredArgsConstructor;
import my.redis.global.intereptor.AuthenticationInterceptor;
import my.redis.jwt.repository.RefreshRepository;
import my.redis.jwt.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}
