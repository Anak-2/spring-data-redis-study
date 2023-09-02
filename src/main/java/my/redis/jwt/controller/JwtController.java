package my.redis.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.redis.jwt.CheckAuthHeader;
import my.redis.jwt.service.JwtService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

//    정상 로그인, AccessToken 반환
    @CheckAuthHeader
    @GetMapping("/login")
    public String login(@RequestParam("username") String username, HttpServletResponse response) throws UnsupportedEncodingException {

        log.info("Call Get Login");

        return jwtService.handleLogin(username, response);
    }

//    재 로그인
    @PostMapping("/reLogin")
    public String reLogin(HttpServletRequest request, HttpServletResponse response){
        log.info("refreshToken = {}",request.getParameter("refreshToken"));
        return "reLogin";
    }
}
