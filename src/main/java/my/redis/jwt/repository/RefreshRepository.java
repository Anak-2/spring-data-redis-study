package my.redis.jwt.repository;

public interface RefreshRepository {

    void saveRefresh(String username, String refreshToken);

    String getRefresh(String username);

    void deleteRefresh(String username);
}
