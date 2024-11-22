package ppl.server.iam.authn.exception.handling;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class EnhancedExceptionHandlingConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<ExceptionHandlingConfigurer<H>, H> {

    private final ExceptionHandlingConfigurer<H> delegate = new ExceptionHandlingConfigurer<>();
    private final Set<AuthenticationEntryPoint> entryPoints = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<AccessDeniedHandler> deniedHandlers = Collections.newSetFromMap(new IdentityHashMap<>());

    public EnhancedExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(AuthenticationEntryPoint entryPoint,
                                                                             RequestMatcher preferredMatcher) {
        entryPoints.add(entryPoint);
        delegate.defaultAuthenticationEntryPointFor(entryPoint, preferredMatcher);
        return this;
    }

    public EnhancedExceptionHandlingConfigurer<H> authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        entryPoints.add(authenticationEntryPoint);
        delegate.authenticationEntryPoint(authenticationEntryPoint);
        return this;
    }

    public EnhancedExceptionHandlingConfigurer<H> defaultAccessDeniedHandlerFor(AccessDeniedHandler deniedHandler,
                                                                        RequestMatcher preferredMatcher) {
        deniedHandlers.add(deniedHandler);
        delegate.defaultAccessDeniedHandlerFor(deniedHandler, preferredMatcher);
        return this;
    }

    public EnhancedExceptionHandlingConfigurer<H> accessDeniedPage(String accessDeniedUrl) {
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage(accessDeniedUrl);
        return accessDeniedHandler(accessDeniedHandler);
    }

    public EnhancedExceptionHandlingConfigurer<H> accessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        deniedHandlers.add(accessDeniedHandler);
        delegate.accessDeniedHandler(accessDeniedHandler);
        return this;
    }

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
        delegate.init(builder);
    }

    @Override
    public void configure(H builder) throws Exception {
        entryPoints.forEach(this::postProcess);
        deniedHandlers.forEach(this::postProcess);
        super.configure(builder);
        delegate.configure(builder);
    }
}
