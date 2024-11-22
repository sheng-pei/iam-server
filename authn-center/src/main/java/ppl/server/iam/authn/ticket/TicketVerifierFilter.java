package ppl.server.iam.authn.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ppl.common.utils.string.Strings;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class TicketVerifierFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/cas/verifyTicket",
            "GET");
    private static final String DEFAULT_TICKET_PARAMETER = "ticket";

    private AntPathRequestMatcher requestMatcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;
    private String ticketParameter = DEFAULT_TICKET_PARAMETER;
    private RedisTicket ticket;
    private Writers writers;
    private Rcs rcs;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Objects.requireNonNull(this.requestMatcher, "requestMatcher is required.");
        if (Strings.isBlank(this.ticketParameter)) {
            this.ticketParameter = DEFAULT_TICKET_PARAMETER;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ticket = request.getParameter(ticketParameter);
        if (ticket == null) {
            throw new InvalidTicketException("No ticket given.");
        }
        String value = this.ticket.getValue(ticket);
        if (value == null) {
            throw new InvalidTicketException("Invalid ticket: " + ticket);
        }

        writers.http(response)
                .json()
                .write(rcs.success(value));
    }

    public void setRequestMatcher(AntPathRequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setTicketParameter(String ticketParameter) {
        this.ticketParameter = ticketParameter;
    }

    @Autowired
    public void setTicket(RedisTicket ticket) {
        this.ticket = ticket;
    }

    @Autowired
    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    @Autowired
    public void setRcs(Rcs rcs) {
        this.rcs = rcs;
    }
}
