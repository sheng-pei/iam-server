package ppl.server.iam.authn.exception.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(RAuthenticationEntryPoint.class);

    private Writers writers;
    private Rcs rcs;

    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    public void setRcs(Rcs rcs) {
        this.rcs = rcs;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Enter authentication entry point.", authException);
        writers.http(response)
                .json()
                .write(rcs.fromException(authException));
    }
}
