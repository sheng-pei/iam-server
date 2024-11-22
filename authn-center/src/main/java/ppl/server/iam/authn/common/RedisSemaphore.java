package ppl.server.iam.authn.common;

import ppl.server.base.Redis;

import java.util.concurrent.TimeUnit;

public class RedisSemaphore {

    private static final int DEFAULT_MAX_RESOURCES = 3;
    private static final int DEFAULT_EXPIRE_MINUTES = 3;

    private final Redis redis;
    private final String name;
    private int maxResources;
    private int expireSeconds;

    public RedisSemaphore(String name, Redis redis) {
        this.name = name;
        this.redis = redis;
        this.maxResources = DEFAULT_MAX_RESOURCES;
        this.expireSeconds = DEFAULT_EXPIRE_MINUTES * 60;
    }

    public void setMaxResources(int maxResources) {
        this.maxResources = maxResources;
    }

    public void setExpireMinutes(int expireMinutes) {
        this.expireSeconds = expireMinutes * 60;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public int getReleaseMinutes() {
        return expireSeconds / 60 + 1;
    }

    public boolean available(String username) {
        String key = name + username;
        String value = redis.get(key);
        int cnt = (value == null ? 0 : Integer.parseInt(value));
        return cnt != maxResources;
    }

    /**
     * Acquire resource and refresh expire time.
     */
    public boolean acquire(String username) {
        String key = name + username;
        redis.setIfAbsent(key, "0", expireSeconds, TimeUnit.SECONDS);
        return redis.incrementUnderTop(key, maxResources, expireSeconds, TimeUnit.SECONDS);
    }

    public boolean release(String username) {
        String key = name + username;
        redis.setIfAbsent(key, "0", expireSeconds, TimeUnit.SECONDS);
        return redis.decrementOverBottom(key, 0, expireSeconds, TimeUnit.SECONDS);
    }

    public void clear(String username) {
        String key = name + username;
        redis.delete(key);
    }
}
