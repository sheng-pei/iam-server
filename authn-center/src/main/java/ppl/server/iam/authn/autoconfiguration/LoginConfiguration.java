package ppl.server.iam.authn.autoconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppl.server.base.Redis;
import ppl.server.iam.authn.common.RedisSemaphore;
import ppl.server.iam.authn.login.reader.random.RandomManager;
import ppl.server.iam.authn.login.reader.random.RedisRandomManager;

@Configuration
//@AutoConfiguration
public class LoginConfiguration {

    @Value("${login.scope:login:}")
    private String loginScope;

    @Value("${login.expire-minutes:3}")
    private int expireMinutes;

    @Value("${login.max-attempts:3}")
    private int maxAttempts;

    @Bean
    public RedisSemaphore loginSemaphore(Redis redis) {
        RedisSemaphore loginSemaphore = new RedisSemaphore(loginScope, redis);
        loginSemaphore.setExpireMinutes(expireMinutes);
        loginSemaphore.setMaxResources(maxAttempts);
        return loginSemaphore;
    }

    @Bean
    public RandomManager randomManager(Redis redis) {
        return new RedisRandomManager(loginScope + "random-manager:", redis);
    }
}
