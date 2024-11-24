package ppl.server.iam.authn.bo;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class EnhancedUser implements UserDetails, CredentialsContainer {
    private final Long id;
    private final String username;
    private String password;
    private final String name;
    private final String email;
    private final String phone;
    private final List<Organization> organizations;
    private final Date expires;
    private final boolean enabled;
    private final boolean locked;
    private final List<PermissionCode> permissionCodes;

    public EnhancedUser(Builder builder) {
        Objects.requireNonNull(builder.id);
        Objects.requireNonNull(builder.username);
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.organizations = builder.organizations == null ? Collections.emptyList() : Collections.unmodifiableList(builder.organizations);
        this.enabled = builder.enabled != null && builder.enabled;
        this.locked = builder.locked != null && builder.locked;
        this.permissionCodes = builder.permissionCodes == null ? Collections.emptyList() : Collections.unmodifiableList(builder.permissionCodes);
        this.expires = builder.expires;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissionCodes;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return expires == null || expires.after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String email;
        private String phone;
        private List<Organization> organizations;
        private Date expires;
        private Boolean enabled;
        private Boolean locked;
        private List<PermissionCode> permissionCodes;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder organizations(List<Organization> organizations) {
            this.organizations = new ArrayList<>(organizations);
            return this;
        }

        public Builder expires(Date expires) {
            this.expires = expires;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder locked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder permissionCodes(List<PermissionCode> permissionCodes) {
            this.permissionCodes = new ArrayList<>(permissionCodes);
            return this;
        }

        public EnhancedUser build() {
            return new EnhancedUser(this);
        }
    }
}
