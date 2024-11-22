package ppl.server.iam.authn.exception.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(RAccessDeniedHandler.class);
    private Writers writers;
    private Rcs rcs;

    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    public void setRcs(Rcs rcs) {
        this.rcs = rcs;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied", accessDeniedException);
        writers.http(response)
                .json()
                .write(rcs.fromException(accessDeniedException));
    }
}
