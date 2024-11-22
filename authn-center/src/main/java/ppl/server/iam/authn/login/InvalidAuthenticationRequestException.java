package ppl.server.iam.authn.login;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationRequestException extends AuthenticationException {
    public InvalidAuthenticationRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
