package ppl.server.iam.authn.authentication.password;

import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ppl.common.utils.string.Strings;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class ClientSaltFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/cas/salt",
            "GET");
    private static final String DEFAULT_USERNAME_PARAMETER = "username";

    private AntPathRequestMatcher matcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;
    private String usernameParameter = DEFAULT_USERNAME_PARAMETER;
    private final ClientSaltPublisher clientSaltPublisher;

    public ClientSaltFilter(ClientSaltPublisher clientSaltPublisher) {
        this.clientSaltPublisher = clientSaltPublisher;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Objects.requireNonNull(clientSaltPublisher, "clientSaltPublisher is required.");
        if (Strings.isBlank(usernameParameter)) {
            this.usernameParameter = DEFAULT_USERNAME_PARAMETER;
        }
    }

    public void setSaltUrl(String url) {
        this.matcher = new AntPathRequestMatcher(url, "GET");
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!matcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getParameter(usernameParameter);
        username = username == null ? "" : username;
        String salt = clientSaltPublisher.publishTo(username);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.getWriter().println(salt);
    }
}
