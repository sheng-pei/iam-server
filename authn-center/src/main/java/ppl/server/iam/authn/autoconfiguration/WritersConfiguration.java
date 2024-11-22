package ppl.server.iam.authn.autoconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppl.server.base.webmvc.response.writer.Writers;

@Configuration
//@AutoConfiguration
public class WritersConfiguration {
    @Bean
    public Writers writers(ObjectMapper mapper) {
        Writers writers = new Writers();
        writers.setMapper(mapper);
        return writers;
    }
}
