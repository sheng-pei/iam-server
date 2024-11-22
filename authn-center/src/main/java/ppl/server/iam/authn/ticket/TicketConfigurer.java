package ppl.server.iam.authn.ticket;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class TicketConfigurer<H extends HttpSecurityBuilder<H>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, H> {

    private final TicketRequestFilter ticketRequestFilter = new TicketRequestFilter();
    private final TicketVerifierFilter ticketVerifierFilter = new TicketVerifierFilter();

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(H builder) throws Exception {
        super.configure(builder);
        postProcess(ticketRequestFilter);
        postProcess(ticketVerifierFilter);
        builder.addFilterBefore(ticketRequestFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(ticketVerifierFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public TicketConfigurer<H> requestMatcher(AntPathRequestMatcher matcher) {
        ticketRequestFilter.setRequestMatcher(matcher);
        return this;
    }

    public TicketConfigurer<H> verifierMatcher(AntPathRequestMatcher matcher) {
        ticketVerifierFilter.setRequestMatcher(matcher);
        return this;
    }

    public TicketConfigurer<H> loginPage(String loginPage) {
        ticketRequestFilter.setLoginPage(loginPage);
        return this;
    }

    public TicketConfigurer<H> ssoPage(String ssoPage) {
        ticketRequestFilter.setSsoPage(ssoPage);
        return this;
    }

    public TicketConfigurer<H> serviceParameter(String serviceParameter) {
        ticketRequestFilter.setServiceParameter(serviceParameter);
        return this;
    }

    public TicketConfigurer<H> ticketParameter(String ticketParameter) {
        ticketRequestFilter.setTicketParameter(ticketParameter);
        ticketVerifierFilter.setTicketParameter(ticketParameter);
        return this;
    }
}
