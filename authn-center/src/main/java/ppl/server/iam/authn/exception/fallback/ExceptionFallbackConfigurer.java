package ppl.server.iam.authn.exception.fallback;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

public class ExceptionFallbackConfigurer<H extends HttpSecurityBuilder<H>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, H> {

    private final SecurityFallbackExceptionFilter filter = new SecurityFallbackExceptionFilter();

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(H builder) throws Exception {
        super.configure(builder);
        postProcess(filter);
        builder.addFilterAfter(filter, DisableEncodeUrlFilter.class);
    }
}
