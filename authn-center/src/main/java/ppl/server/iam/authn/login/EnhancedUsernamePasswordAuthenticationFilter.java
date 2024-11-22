package ppl.server.iam.authn.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ppl.server.iam.authn.common.RedisSemaphore;
import ppl.server.iam.authn.login.reader.InvalidRequestException;
import ppl.server.iam.authn.login.reader.LoginStringDecryptedException;
import ppl.server.iam.authn.login.reader.ReplayAttackException;
import ppl.server.iam.authn.login.reader.UnauthenticatedUsernamePasswordAuthenticationTokenReader;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EnhancedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(EnhancedUsernamePasswordAuthenticationFilter.class);

    private static final String UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE =
            EnhancedUsernamePasswordAuthenticationFilter.class.getName() + ".request";

    private static final UsernamePasswordAuthenticationToken NULL_TOKEN = new UsernamePasswordAuthenticationToken(null, null) {
        @Override
        public void eraseCredentials() {
        }

        @Override
        public void setDetails(Object details) {
        }

        @Override
        public boolean implies(Subject subject) {
            return false;
        }
    };

    private RedisSemaphore loginSemaphore;
    private UnauthenticatedUsernamePasswordAuthenticationTokenReader<?> tokenReader;

    public EnhancedUsernamePasswordAuthenticationFilter() {
        super.setPostOnly(true);
    }

    public EnhancedUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        super.setPostOnly(true);
    }

    public RedisSemaphore getLoginSemaphore() {
        return loginSemaphore;
    }

    @Autowired
    public void setLoginSemaphore(RedisSemaphore loginSemaphore) {
        this.loginSemaphore = loginSemaphore;
    }

    public UnauthenticatedUsernamePasswordAuthenticationTokenReader<?> getTokenReader() {
        return tokenReader;
    }

    public void setTokenReader(UnauthenticatedUsernamePasswordAuthenticationTokenReader<?> tokenReader) {
        this.tokenReader = tokenReader;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        if (tokenReader != null) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getAttribute(
                    UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE);
            if (token == null) {
                token = tokenReader.readFrom(request);
                if (token == null) {
                    token = NULL_TOKEN;
                }
                request.setAttribute(UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE, token);
            }
            return (String) token.getCredentials();
        } else {
            return super.obtainPassword(request);
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username;
        if (tokenReader != null) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getAttribute(
                    UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE);
            if (token == null) {
                token = tokenReader.readFrom(request);
                if (token == null) {
                    token = NULL_TOKEN;
                }
                request.setAttribute(UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE, token);
            }
            username = (String) token.getPrincipal();
        } else {
            username = super.obtainUsername(request);
        }
        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            throw new AlreadyAuthenticatedException("Duplicate authentication.");
        }

        try {
            String username = obtainUsername(request);
            if (loginSemaphore != null) {
                loginSemaphore.acquire(username);
            }
            Authentication ret = super.attemptAuthentication(request, response);
            if (loginSemaphore != null) {
                try {
                    loginSemaphore.clear(username);
                } catch (Throwable t) {
                    log.info("Redis error during clear login semaphore status.");
                }
            }
            return ret;
        } catch (ReplayAttackException | InvalidRequestException | LoginStringDecryptedException e) {
            throw new InvalidAuthenticationRequestException("Error when obtaining username and password.", e);
        } finally {
            request.removeAttribute(UNAUTHENTICATED_USERNAME_PASSWORD_AUTHENTICATION_TOKEN_ATTRIBUTE);
        }
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        if (!postOnly) {
            throw new IllegalArgumentException(
                    "Only HTTP POST requests will be allowed by " +
                            "EnhancedUsernamePasswordAuthenticationFilter.");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContext context = null;
        if (failed instanceof AlreadyAuthenticatedException) {//retain security context
            context = SecurityContextHolder.getContext();
        }
        try {
            super.unsuccessfulAuthentication(request, response, failed);
        } finally {
            SecurityContextHolder.setContext(context);
        }
    }
}
