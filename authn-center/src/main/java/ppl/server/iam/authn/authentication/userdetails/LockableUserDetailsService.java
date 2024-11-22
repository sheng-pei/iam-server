package ppl.server.iam.authn.authentication.userdetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ppl.server.iam.authn.common.RedisSemaphore;

import java.util.Collections;

//TODO,原始密码不超过72字节
public class LockableUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(LockableUserDetailsService.class);

    private RedisSemaphore loginSemaphore;

    public RedisSemaphore getLoginSemaphore() {
        return loginSemaphore;
    }

    @Autowired
    public void setLoginSemaphore(RedisSemaphore loginSemaphore) {
        this.loginSemaphore = loginSemaphore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean locked = true;
        try {
            locked = loginSemaphore != null && !loginSemaphore.available(username);
        } catch (Throwable t) {
            log.info("Redis error during check whether user is locked, fallback to locked.");
        }
        if (locked) {
            return lockedUser(username);
        }

        User user = mockUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("Unknown username.");
        }
        return user;
    }

    private User lockedUser(String username) {
        return new User(username, "",
                true, true, true,
                false, Collections.emptyList());
    }

    private User mockUser(String username) {
        if (username.equals("user@dtstack.com")) {
            return new User(username, "{SQaLqBBIE5zlONwHrY4t/udtjsD2WXzheR2bBGnRmlo=}a9556f7c5cfba2ac31b442d79598ba722d1fb8b4ac073c0822c1850b7e248d3b", true, true,
                    true, true,
                    Collections.emptyList());
        } else if (username.equals("disabled@dtstack.com")) {
            return new User(username, "{2krMi6aRXVvz6jjpaQbQZwws7adt6+9VgbRxzXObW2k=}fb5373414954a68b7114a4d34ac6f70e610f753c5df2a7bdf06bcb00d2131288", false,
                    true, true, true,
                    Collections.emptyList());
        } else if (username.equals("expired@dtstack.com")) {
            return new User(username, "{MLukHXr5OdsgWoDGOE7QhvyBEayy1jYn+waQTR8lz4I=}71d1a5f7d11ba746248ea55f4351a6826bdf55e297a6203364a870a68cde495e", true,
                    false, true, true,
                    Collections.emptyList());
        } else if (username.equals("pass_expired@dtstack.com")) {
            return new User(username, "{yMaH0+g41ZMsvOhKa1WJ2FNU6B3SQavglNEsvm2T0Ws=}3344f05e2dd7e54d312868d7ff3bb757af224221a394f94ed307e6f5f0602fca", true,
                    true, false, true,
                    Collections.emptyList());
        }
        return null;
    }
}
