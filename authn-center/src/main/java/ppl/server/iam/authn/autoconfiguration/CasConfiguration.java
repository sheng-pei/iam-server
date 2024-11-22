package ppl.server.iam.authn.autoconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppl.common.utils.string.Strings;
import ppl.server.base.Redis;
import ppl.server.iam.authn.ticket.RedisTicket;

@Configuration
//@AutoConfiguration
public class CasConfiguration {
    @Value("${cas.ticket.scope}")
    private String ticketScope;

    @Bean
    public RedisTicket ticket(Redis redis) {
        if (Strings.isBlank(ticketScope)) {
            return new RedisTicket(redis);
        }
        return new RedisTicket(ticketScope.trim(), redis);
    }
}
