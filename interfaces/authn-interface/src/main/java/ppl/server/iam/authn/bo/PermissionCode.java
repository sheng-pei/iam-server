package ppl.server.iam.authn.bo;

import org.springframework.security.core.GrantedAuthority;

public class PermissionCode implements GrantedAuthority {
    private final String code;

    public PermissionCode(String code) {
        this.code = code;
    }

    @Override
    public String getAuthority() {
        return code;
    }
}
