package my.redis.global.intereptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.redis.jwt.JwtTokenProvider;
import my.redis.jwt.service.JwtService;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws RuntimeException {

        try{
            String accessToken = request.getHeader("Authorization");
            JwtTokenProvider.checkAccessToken(accessToken);

            Cookie[] cookies = request.getCookies();

            if(cookies == null) return false;

            List<Cookie> refreshCookieList = Arrays.stream(cookies).filter(cookie ->
                    cookie.getName().equals("refreshToken")
            ).toList();

            String refreshToken = refreshCookieList.get(0).getValue();

            String refreshAccessToken = JwtTokenProvider.refreshAccessToken(accessToken, refreshToken);

            request.setAttribute("refreshToken", refreshAccessToken);
        }catch (RuntimeException r){
            return false;
        }

        return true;
    }
}
