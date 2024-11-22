package ppl.server.iam.authn.login.reader;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UnauthenticatedUsernamePasswordAuthenticationTokenReader<H extends HttpSecurityBuilder<H>> {
    void postProcess(ObjectPostProcessor<Object> processor);

    void addFilters(H builder);

    UsernamePasswordAuthenticationToken readFrom(HttpServletRequest request);
}
