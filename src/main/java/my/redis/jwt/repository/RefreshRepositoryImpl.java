package my.redis.jwt.repository;

import jakarta.annotation.Resource;
import my.redis.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshRepositoryImpl implements RefreshRepository{

    private final String hashReference = "RefreshToken";

    @Resource(name = "redisTemplate")
    HashOperations<String, String, String> hashOperations;


    @Override
    public void saveRefresh(String username, String refreshToken) {
        refreshToken = refreshToken.replace(JwtTokenProvider.BEARER_TYPE, "");
        hashOperations.putIfAbsent(hashReference, username, refreshToken);
    }

    @Override
    public String getRefresh(String username) {
        return hashOperations.get(hashReference, username);
    }

    @Override
    public void deleteRefresh(String username) {
        hashOperations.delete(hashReference, username);
    }
}
