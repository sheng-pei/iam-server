package ppl.server.iam.authn.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import ppl.server.base.Redis;

@Configuration
//@AutoConfiguration
public class RedisConfiguration {
    @Bean
    public Redis redis(StringRedisTemplate redisTemplate) {
        return new Redis(redisTemplate);
    }
}
