package ppl.server.iam.authn.authentication.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import ppl.server.iam.authn.common.RedisSemaphore;

public class PreAuthenticationChecks implements UserDetailsChecker {
    private final RedisSemaphore loginSemaphore;

    public PreAuthenticationChecks(RedisSemaphore loginSemaphore) {
        this.loginSemaphore = loginSemaphore;
    }

    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new UserLockedException(loginSemaphore.getReleaseMinutes(), "User account is locked.");
        }
    }
}
