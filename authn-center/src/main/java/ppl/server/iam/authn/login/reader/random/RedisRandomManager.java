package ppl.server.iam.authn.login.reader.random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.security.SecureRandom;
import ppl.common.utils.string.Strings;
import ppl.server.base.Redis;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class RedisRandomManager implements RandomManager {

    private static final Logger log = LoggerFactory.getLogger(RedisRandomManager.class);
    private static final int DEFAULT_RANDOM_LENGTH = 15;
    private static final int DEFAULT_EXPIRE_SECONDS = 120;
    private static final String DEFAULT_SCOPE = "cas:login:random:";

    private final Redis redis;
    private int randomLength;
    private int expireSeconds;
    private String scope;

    public RedisRandomManager(Redis redis) {
        this.redis = redis;
        this.randomLength = DEFAULT_RANDOM_LENGTH;
        this.expireSeconds = DEFAULT_EXPIRE_SECONDS;
        this.scope = DEFAULT_SCOPE;
    }

    public RedisRandomManager(String scope, Redis redis) {
        this.redis = redis;
        this.randomLength = DEFAULT_RANDOM_LENGTH;
        this.expireSeconds = DEFAULT_EXPIRE_SECONDS;
        this.scope = scope;
    }

    public void setRandomLength(int randomLength) {
        if (randomLength > 0) {
            this.randomLength = randomLength;
        } else {
            log.info("Ignore non-positive random length.");
        }
    }

    public void setExpireSeconds(int expireSeconds) {
        if (expireSeconds > 0) {
            this.expireSeconds = expireSeconds;
        } else {
            log.info("Ignore non-positive expire seconds.");
        }
    }

    public void setScope(String scope) {
        if (Strings.isNotBlank(scope)) {
            this.scope = scope;
        } else {
            log.info("Ignore blank scope.");
        }
    }

    @Override
    public String create(String prefix) {
        String r = Base64.getEncoder().encodeToString(
                SecureRandom.defStrong().nextBytes(randomLength));
        String random = prefix + r;
        try {
            redis.set(scope + random, random, expireSeconds, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.info("Couldn't save new random: " + random + ".", t);
        }
        return random;
    }

    @Override
    public boolean check(String random) {
        try {
            return redis.getAndDelete(scope + random) != null;
        } catch (Throwable t) {
            log.info("Redis error during checking random.", t);
            return false;
        }
    }

}
