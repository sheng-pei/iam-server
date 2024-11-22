package ppl.server.iam.authn.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppl.server.iam.authn.common.keys.KeyCenter;
import ppl.server.iam.authn.common.keys.LocalConfigKeyCenter;

//@AutoConfiguration
@Configuration
public class KeyCenterConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "key-center.local")
    public KeyCenter localKeyCenter() {
        return new LocalConfigKeyCenter();
    }
}
