package my.redis.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class CheckAuthHeaderAOP {

    private static final String accessHeader = "Authorization";

    @Around("@annotation(my.redis.jwt.CheckAuthHeader)")
    public Object checkAuthHeaderAOP(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("Call Check Auth Header");

        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = request.getHeader(accessHeader);

        log.info("Access Token: {}",accessToken);

        if(accessToken == null) return joinPoint.proceed();

        if(!JwtTokenProvider.checkAccessToken(accessToken)) return joinPoint.proceed();

        Cookie[] cookies = request.getCookies();

        if(cookies == null) return joinPoint.proceed();

        List<Cookie> refreshCookieList = Arrays.stream(cookies).filter(cookie ->
                cookie.getName().equals("refreshToken")
        ).toList();

        String refreshToken = refreshCookieList.get(0).getValue();

        String refreshAccessToken = JwtTokenProvider.refreshAccessToken(accessToken, refreshToken);

        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();

        if(response != null) response.setHeader(accessHeader, refreshAccessToken);

        return joinPoint.proceed();
    }
}
