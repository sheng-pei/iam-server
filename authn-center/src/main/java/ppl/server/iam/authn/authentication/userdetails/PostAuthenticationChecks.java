package ppl.server.iam.authn.authentication.userdetails;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class PostAuthenticationChecks implements UserDetailsChecker {
    @Override
    public void check(UserDetails user) {
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled: '" + user.getUsername() + "'.");
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired: '" + user.getUsername() + "'.");
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials have expired: '" + user.getUsername() + "'.");
        }
    }
}
