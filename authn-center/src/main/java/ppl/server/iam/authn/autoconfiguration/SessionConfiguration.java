package ppl.server.iam.authn.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import ppl.server.base.session.CustomSIDRedisIndexedSessionRepository;
import ppl.server.base.session.TGCSessionIdGenerator;

@Configuration
//@AutoConfiguration
public class SessionConfiguration {
    @Bean
    public SessionRepositoryCustomizer<CustomSIDRedisIndexedSessionRepository> customSessionIdGenerator() {
        return r -> r.setSessionIdGenerator(new TGCSessionIdGenerator());
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("CASTGC");
        cookieSerializer.setUseBase64Encoding(false);
        return cookieSerializer;
    }
}
