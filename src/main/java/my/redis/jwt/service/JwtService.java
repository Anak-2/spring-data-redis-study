package my.redis.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.redis.jwt.JwtTokenProvider;
import my.redis.jwt.repository.RefreshRepository;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

public interface JwtService {

    String handleLogin(String username, HttpServletResponse response) throws UnsupportedEncodingException;
}
