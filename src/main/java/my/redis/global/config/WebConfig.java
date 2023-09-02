package my.redis.global.config;

import lombok.RequiredArgsConstructor;
import my.redis.global.intereptor.AuthenticationInterceptor;
import my.redis.jwt.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtService jwtService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(jwtService))
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}
