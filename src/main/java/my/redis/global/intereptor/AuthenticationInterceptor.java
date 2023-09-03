package my.redis.global.intereptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.redis.jwt.JwtTokenProvider;
import my.redis.jwt.repository.RefreshRepository;
import my.redis.jwt.service.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws RuntimeException {

        try{
            String accessToken = request.getHeader("Authorization");
            accessToken = accessToken.replace(JwtTokenProvider.BEARER_TYPE, "");
            if(JwtTokenProvider.checkAccessToken(accessToken)) return true;

            Cookie[] cookies = request.getCookies();

            if(cookies == null) return false;

            List<Cookie> refreshCookieList = Arrays.stream(cookies).filter(cookie ->
                    cookie.getName().equals("refreshToken")
            ).toList();

            String refreshToken = refreshCookieList.get(0).getValue();
            refreshToken = URLDecoder.decode(refreshToken,"UTF-8");
            refreshToken = refreshToken.replace(JwtTokenProvider.BEARER_TYPE, "");
            String refreshAccessToken = JwtTokenProvider.refreshAccessToken(accessToken, refreshToken);

            log.info("Set RefreshAccessToken: {}", refreshAccessToken);
            request.setAttribute("refreshAccessToken", refreshAccessToken);
        }catch (RuntimeException | UnsupportedEncodingException r){
            log.info("error: {}",r.getMessage());
            return false;
        }

        return true;
    }
}
