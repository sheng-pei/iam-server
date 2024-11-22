package ppl.server.iam.authn.login;

import org.springframework.security.core.AuthenticationException;

public class AlreadyAuthenticatedException extends AuthenticationException {
    public AlreadyAuthenticatedException(String msg) {
        super(msg);
    }
}
