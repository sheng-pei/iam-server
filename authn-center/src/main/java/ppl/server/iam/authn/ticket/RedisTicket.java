package ppl.server.iam.authn.ticket;

import ppl.common.utils.security.SecureRandom;
import ppl.server.base.Redis;
import ppl.server.iam.authn.common.RedisSemaphore;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class RedisTicket {
    private static final String DEFAULT_SCOPE = "cas:login:ticket:";
    private static final int DEFAULT_EXPIRE_MINUTES = 2;
    private static final int DEFAULT_LENGTH = 20;

    private final String scope;
    private final Redis redis;
    private final RedisSemaphore ticketSemaphore;
    private int length = DEFAULT_LENGTH;
    private int expireMinutes;

    public RedisTicket(Redis redis) {
        this(DEFAULT_SCOPE, redis);
    }

    public RedisTicket(String scope, Redis redis) {
        this.scope = scope;
        this.redis = redis;
        this.expireMinutes = DEFAULT_EXPIRE_MINUTES;
        RedisSemaphore ticketSemaphore = new RedisSemaphore(scope + "semaphore:", redis);
        ticketSemaphore.setExpireSeconds(expireMinutes * 60 + 5);
        this.ticketSemaphore = ticketSemaphore;
    }

    public void setExpireMinutes(int expireMinutes) {
        this.expireMinutes = expireMinutes;
        ticketSemaphore.setExpireSeconds(this.expireMinutes * 60 + 5);
    }

    public void setMaxTickets(int maxTickets) {
        ticketSemaphore.setMaxResources(maxTickets);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String generate(String value) {
        if (!ticketSemaphore.acquire(value)) {
            throw new TooManyTicketGeneratedException("Generate too many ticket.", expireMinutes);
        }

        String ticket = "ST-" + Base64.getEncoder().encodeToString(SecureRandom.defStrong().nextBytes(length));
        String key = scope + ticket;
        redis.set(key, value, expireMinutes, TimeUnit.MINUTES);
        return ticket;
    }

    public String getValue(String ticket) {
        String key = scope + ticket;
        String value = redis.getAndDelete(key);
        if (value != null) {
            ticketSemaphore.release(value);
        }
        return value;
    }
}
