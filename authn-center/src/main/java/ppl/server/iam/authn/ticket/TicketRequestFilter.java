package ppl.server.iam.authn.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ppl.common.utils.string.Strings;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TicketRequestFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/cas/ticket",
            "GET");
    private static final String DEFAULT_LOGIN_PAGE = "/login";
    private static final String DEFAULT_SSO_PAGE = "/sso";
    private static final String DEFAULT_SERVICE_PARAMETER = "service";
    private static final String DEFAULT_TICKET_PARAMETER = "ticket";
    private AntPathRequestMatcher requestMatcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;
    private String loginPage = DEFAULT_LOGIN_PAGE;
    private String ssoPage = DEFAULT_SSO_PAGE;
    private String serviceParameter = DEFAULT_SERVICE_PARAMETER;
    private String ticketParameter = DEFAULT_TICKET_PARAMETER;
    private RedisTicket ticket;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Objects.requireNonNull(requestMatcher, "requestMatcher is required.");
        if (Strings.isBlank(loginPage)) {
            this.loginPage = DEFAULT_LOGIN_PAGE;
        }
        if (Strings.isBlank(ssoPage)) {
            this.ssoPage = DEFAULT_SSO_PAGE;
        }
        if (Strings.isBlank(serviceParameter)) {
            this.serviceParameter = DEFAULT_SERVICE_PARAMETER;
        }
        if (Strings.isBlank(ticketParameter)) {
            this.ticketParameter = DEFAULT_TICKET_PARAMETER;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String service = request.getParameter(serviceParameter);
        String url = "%s?" + serviceQuery(service);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            url = String.format(url, ssoPage);
            url = url + ticketParameter + "=" + ticket.generate(authentication.getName());
        } else {
            url = String.format(url, loginPage);
            url = url.substring(0, url.length() - 1);
        }
        response.sendRedirect(url);
    }

    private String serviceQuery(String service) throws UnsupportedEncodingException {
        if (service != null) {
            return serviceParameter + "=" + URLEncoder.encode(service, StandardCharsets.UTF_8.name()) + "&";
        } else {
            return "";
        }
    }

    public void setRequestMatcher(AntPathRequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public void setSsoPage(String ssoPage) {
        this.ssoPage = ssoPage;
    }

    public void setServiceParameter(String serviceParameter) {
        this.serviceParameter = serviceParameter;
    }

    public void setTicketParameter(String ticketParameter) {
        this.ticketParameter = ticketParameter;
    }

    @Autowired
    public void setTicket(RedisTicket ticket) {
        this.ticket = ticket;
    }
}
