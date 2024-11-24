package ppl.server.iam.authn;

import ppl.server.iam.authn.bo.EnhancedUser;

public interface UserService {
    EnhancedUser getByUsername(String username);
}
