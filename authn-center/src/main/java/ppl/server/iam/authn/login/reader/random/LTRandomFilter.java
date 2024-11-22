package ppl.server.iam.authn.login.reader.random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class LTRandomFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/cas/random",
            "GET");
    private static final String DEFAULT_PREFIX = "LT-";

    private AntPathRequestMatcher matcher;
    private RandomManager randomManager;
    private Writers writers;

    public LTRandomFilter() {
        this(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public LTRandomFilter(AntPathRequestMatcher matcher) {
        Objects.requireNonNull(matcher);
        this.matcher = matcher;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Objects.requireNonNull(matcher, "Matcher is required.");
        Objects.requireNonNull(randomManager, "RandomManager is required.");
    }

    public void randomUrl(String url) {
        this.matcher = new AntPathRequestMatcher(url, "GET");
    }

    public void setRandomManager(RandomManager randomManager) {
        this.randomManager = randomManager;
    }

    @Autowired
    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!matcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String r = randomManager.create(DEFAULT_PREFIX);
        writers.http(response)
                .plain()
                .write(r);
    }
}
