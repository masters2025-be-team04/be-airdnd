package rice_monkey.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redis;
    private static final Duration TTL = Duration.ofSeconds(30);

    public boolean acquire(String key) {
        Boolean result = redis.opsForValue().setIfAbsent(key, "locked", TTL);

        return result != null && result;
    }

    public void release(String key) {
        redis.delete(key);
    }

}
