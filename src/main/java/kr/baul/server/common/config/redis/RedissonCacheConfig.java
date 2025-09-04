package kr.baul.server.common.config.redis;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedissonCacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        var topSelling10m = new CacheConfig(Duration.ofMinutes(10).toMillis(), 0);
        return new RedissonSpringCacheManager(
                redissonClient,
                Map.of("topSellingItems.v1", topSelling10m)
        );
    }

}
