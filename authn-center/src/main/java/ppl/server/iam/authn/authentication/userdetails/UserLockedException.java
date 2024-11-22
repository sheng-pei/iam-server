package ppl.server.iam.authn.authentication.userdetails;

import org.springframework.security.authentication.LockedException;
import ppl.server.base.webmvc.response.r.MessageParameter;

public class UserLockedException extends LockedException implements MessageParameter {
    private final int releaseMinutes;

    public UserLockedException(int releaseMinutes, String msg) {
        super(msg);
        this.releaseMinutes = releaseMinutes;
    }

    @Override
    public Object[] params() {
        return new Object[]{releaseMinutes};
    }
}
