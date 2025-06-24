package rice_monkey.booking.common.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisProperties redisProperties;

    @Bean(name = "redisson", destroyMethod = "shutdown")
    @Primary
    public RedissonClient redisson() {
        Config cfg = new Config();
        var single = cfg
                .useSingleServer()
                .setAddress("redis://" +
                        redisProperties.getHost() + ":" +
                        redisProperties.getPort())
                .setConnectionMinimumIdleSize(8)
                .setTimeout(3000);

        // 비밀번호가 설정되어 있을 때만 AUTH
        String pw = redisProperties.getPassword();
        if (pw != null && !pw.isBlank()) {
            single.setPassword(pw);
        }
        return Redisson.create(cfg);
    }

}
