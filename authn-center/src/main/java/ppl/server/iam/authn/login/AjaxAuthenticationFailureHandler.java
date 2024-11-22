package ppl.server.iam.authn.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(AjaxAuthenticationFailureHandler.class);

    private Writers writers;
    private Rcs rcs;

    @Autowired
    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    @Autowired
    public void setRcs(Rcs rcs) {
        this.rcs = rcs;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("Authentication error.", exception);
        writers.http(response)
                .json()
                .write(rcs.fromException(exception));
    }
}
