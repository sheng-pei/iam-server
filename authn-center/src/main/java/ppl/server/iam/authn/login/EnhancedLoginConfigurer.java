package ppl.server.iam.authn.login;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ppl.server.iam.authn.common.RedisSemaphore;
import ppl.server.iam.authn.login.reader.UnauthenticatedUsernamePasswordAuthenticationTokenReader;

public class EnhancedLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, EnhancedLoginConfigurer<H>, EnhancedUsernamePasswordAuthenticationFilter> {

    private boolean disableDefaultLoginPage;
    private UnauthenticatedUsernamePasswordAuthenticationTokenReader<H> tokenReader;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    public EnhancedLoginConfigurer() {
        super(new EnhancedUsernamePasswordAuthenticationFilter(), null);
        usernameParameter("username");
        passwordParameter("password");
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
        if (!disableDefaultLoginPage) {
            initDefaultLoginFilter(http);
        }
    }

    @Override
    public void configure(H http) throws Exception {
        if (failureHandler != null) {
            postProcess(failureHandler);
        }
        if (successHandler != null) {
            postProcess(successHandler);
        }

        super.configure(http);
        RedisSemaphore current = getAuthenticationFilter().getLoginSemaphore();
        RedisSemaphore shared = http.getSharedObject(RedisSemaphore.class);
        if (shared == null) {
            if (current != null) {
                http.setSharedObject(RedisSemaphore.class, current);
            }
        } else {
            if (current == null) {
                loginSemaphore(shared);
            } else if (current != shared) {
                throw new IllegalStateException("Login semaphore is conflict.");
            }
        }

        postProcess(tokenReader);
        tokenReader.postProcess(this::postProcess);
        tokenReader.addFilters(http);
    }

    public EnhancedLoginConfigurer<H> disableDefaultLoginPage() {
        this.disableDefaultLoginPage = true;
        return this;
    }

    public EnhancedLoginConfigurer<H> tokenReader(UnauthenticatedUsernamePasswordAuthenticationTokenReader<H> tokenReader) {
        this.tokenReader = tokenReader;
        getAuthenticationFilter().setTokenReader(tokenReader);
        return this;
    }

    public EnhancedLoginConfigurer<H> postProcessedSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        successHandler(successHandler);
        return this;
    }

    public EnhancedLoginConfigurer<H> postProcessedFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        failureHandler(failureHandler);
        return this;
    }

    public EnhancedLoginConfigurer<H> loginSemaphore(RedisSemaphore loginSemaphore) {
        getAuthenticationFilter().setLoginSemaphore(loginSemaphore);
        return this;
    }

    public EnhancedLoginConfigurer<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    public EnhancedLoginConfigurer<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    @Override
    public EnhancedLoginConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }

    private void initDefaultLoginFilter(H http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }
}
