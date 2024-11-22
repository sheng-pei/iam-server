package ppl.server.iam.authn.authentication;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.session.ForceEagerSessionCreationFilter;
import ppl.server.iam.authn.common.RedisSemaphore;
import ppl.server.iam.authn.authentication.password.ClientSaltFilter;
import ppl.server.iam.authn.authentication.password.ClientSaltPublisher;
import ppl.server.iam.authn.authentication.userdetails.LockableUserDetailsService;

public class AuthenticationProviderConfigurer<H extends HttpSecurityBuilder<H>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, H> {

    private final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    private UserDetailsService userDetailsService;
    private ClientSaltFilter clientSaltFilter;

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(H builder) throws Exception {
        super.configure(builder);
        postProcess(this.userDetailsService);
        if (userDetailsService instanceof LockableUserDetailsService) {
            LockableUserDetailsService lockableUserDetailsService = (LockableUserDetailsService) userDetailsService;
            RedisSemaphore current = lockableUserDetailsService.getLoginSemaphore();
            RedisSemaphore shared = builder.getSharedObject(RedisSemaphore.class);
            if (shared == null) {
                if (current != null) {
                    builder.setSharedObject(RedisSemaphore.class, current);
                }
            } else {
                if (current == null) {
                    lockableUserDetailsService.setLoginSemaphore(shared);
                } else if (current != shared) {
                    throw new IllegalStateException("Login semaphore is conflict.");
                }
            }
        }
        builder.authenticationProvider(this.authenticationProvider);
        postProcess(this.clientSaltFilter);
        builder.addFilterBefore(this.clientSaltFilter, ForceEagerSessionCreationFilter.class);
    }

    public AuthenticationProviderConfigurer<H> passwordEncoder(PasswordEncoder passwordEncoder) {
        this.authenticationProvider.setPasswordEncoder(passwordEncoder);
        return this;
    }

    public AuthenticationProviderConfigurer<H> clientSaltPublisher(ClientSaltPublisher clientSaltPublisher) {
        this.clientSaltFilter = new ClientSaltFilter(clientSaltPublisher);
        return this;
    }

    public AuthenticationProviderConfigurer<H> userDetailsPasswordManager(UserDetailsPasswordService passwordManager) {
        this.authenticationProvider.setUserDetailsPasswordService(passwordManager);
        return this;
    }

    public AuthenticationProviderConfigurer<H> userDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.authenticationProvider.setUserDetailsService(userDetailsService);
        return this;
    }

    public AuthenticationProviderConfigurer<H> preAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.authenticationProvider.setPreAuthenticationChecks(preAuthenticationChecks);
        return this;
    }

    public AuthenticationProviderConfigurer<H> postAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.authenticationProvider.setPostAuthenticationChecks(postAuthenticationChecks);
        return this;
    }

}
