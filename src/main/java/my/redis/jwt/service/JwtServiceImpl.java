package my.redis.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.redis.employee.domain.Employee;
import my.redis.jwt.JwtTokenProvider;
import my.redis.jwt.repository.RefreshRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> vop;

    private final RefreshRepository refreshRepository;
    private static final String accessHeader = "Authorization";

    @Override
    public String handleLogin(String username, HttpServletResponse response) throws UnsupportedEncodingException {

//        ToDo: Redis 버전 6.2.3 부터 GETDEL 을 지원한다는데 GETDEL 명령어가 없다는 오류 계속 발생
//              출처: https://github.com/redis/redis-doc/issues/1939
//        vop.getAndDelete(username);
        vop.set(username,"",1);

        String accessToken = JwtTokenProvider.generateAccessToken(username);

        String refreshToken = JwtTokenProvider.generateRefreshToken();
        vop.set(username, refreshToken);

        Cookie cookie = new Cookie("refreshToken", URLEncoder.encode(refreshToken, "UTF-8"));
        cookie.setPath("/");
        response.addCookie(cookie);

        return accessToken;
    }

}
