package ppl.server.iam.authn.client;

import ppl.server.iam.authn.UserService;
import ppl.server.iam.authn.bo.EnhancedUser;

public class UserServiceClient implements UserService {
    @Override
    public EnhancedUser getByUsername(String username) {
        return null;
    }
}
