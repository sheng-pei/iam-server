package ppl.server.iam.authn.exception.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFallbackExceptionFilter extends GenericFilterBean {
    private static final Logger log = LoggerFactory.getLogger(SecurityFallbackExceptionFilter.class);

    private final OncePerRequestFilter delegate = new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(request, response);
        }
    };

    private Writers writers;
    private Rcs rcs;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            delegate.doFilter(request, response, chain);
        } catch (ServletException e) {
            throw e;
        } catch (Throwable t) {
            log.info("Internal error.", t);
            writers.http(response)
                    .json()
                    .write(rcs.fromException(t));
        }
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
