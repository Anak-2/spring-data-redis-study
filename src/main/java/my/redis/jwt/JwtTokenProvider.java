package my.redis.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import my.redis.jwt.repository.RefreshRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORITIES_KEY = "auth";
    public static final String BEARER_TYPE = "Bearer ";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 60*1000L; // 1m
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 24*60*60*1000L; // 1d

    private static String secretKey;
    private static RefreshRepository refreshRepository;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshRepository refreshRepository){
        JwtTokenProvider.secretKey = secretKey;
        JwtTokenProvider.refreshRepository = refreshRepository;
    }

    //    AccessToken 생성
    public static String generateAccessToken(String username){
        return BEARER_TYPE + JWT.create()
                .withSubject("jwtStudy")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .withClaim("username", username)
                .withClaim("type",TYPE_ACCESS)
                .sign(HMAC512(secretKey));
    }

    //    RefreshToken 생성
    public static String generateRefreshToken(){
        return BEARER_TYPE + JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .withClaim("type",TYPE_REFRESH)
                .sign(HMAC512(secretKey));
    }

    //   redis 를 이용해 access token 에서 꺼낸 username 으로 refresh token 을 가져오고,
    //   유효하면 refresh token 재발급 & access token 재발급
    public static String refreshAccessToken(String accessToken, String refreshToken){
        try{
            // access token 검증 & username 꺼내기
            DecodedJWT jwt = JWT.decode(accessToken);
            String username = jwt.getClaim("username").asString();

            // refresh token 검증
            if(refreshRepository.getRefresh(username) == null) {
                refreshRepository.deleteRefresh(username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh is different");
            }

            DecodedJWT rJwt = JWT.require(HMAC512(secretKey)).build()
                    .verify(refreshToken);

            // access token 생성
            return generateAccessToken(username);
        }
        catch(TokenExpiredException e){
            log.error("Refresh Token is Expired on "+e.getExpiredOn());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh Token is Expired on "+e.getExpiredOn(),e);

        }catch(SignatureVerificationException sve){
            log.error("Refresh Signature is invalidate");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh Signature is invalidate",sve);
        }
    }

    public static boolean checkAccessToken(String accessToken){
        try{
            DecodedJWT jwt = JWT.require(HMAC512(secretKey)).build()
                    .verify(accessToken);
            return true;
        }
        catch(TokenExpiredException e){
            log.error("Access Token is Expired on "+e.getExpiredOn());
            return false;
        }catch(SignatureVerificationException sve){
            log.error("Access Signature is invalidate");
            return false;
        }
    }

}
