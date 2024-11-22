package ppl.server.iam.authn.autoconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import ppl.server.base.Redis;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;
import ppl.server.iam.authn.common.RedisSemaphore;
import ppl.server.iam.authn.common.keys.KeyCenter;
import ppl.server.iam.authn.exception.fallback.ExceptionFallbackConfigurer;
//import ppl.server.iam.authn.login.*;
//import ppl.server.iam.authn.login.authentication.AuthenticationProviderConfigurer;
//import ppl.server.iam.authn.login.authentication.userdetails.LockableUserDetailsService;
//import ppl.server.iam.authn.login.authentication.userdetails.PostAuthenticationChecks;
//import ppl.server.iam.authn.login.authentication.userdetails.PreAuthenticationChecks;
//import ppl.server.iam.authn.login.authentication.userdetails.password.ClientDigestMessageDigestExtensionPasswordEncoderConfigurer;
//import ppl.server.iam.authn.login.reader.EncryptedUUPATReader;
//import ppl.server.iam.authn.login.reader.UnauthenticatedUsernamePasswordAuthenticationTokenReaderConfigurer;
//import ppl.server.iam.authn.login.reader.random.RandomManager;
//import ppl.server.iam.authn.login.reader.random.RedisRandomManager;
//import ppl.server.iam.authn.login.authentication.userdetails.password.ClientDigestMessageDigestExtensionPasswordEncoderConfigurer;
//import ppl.server.iam.authn.login.reader.EncryptedUUPATReader;
//import ppl.server.iam.authn.login.reader.UnauthenticatedUsernamePasswordAuthenticationTokenReaderConfigurer;
//import ppl.server.iam.authn.login.reader.random.RandomManager;
//import ppl.server.iam.authn.login.reader.random.RedisRandomManager;
import ppl.server.iam.authn.exception.handling.EnhancedExceptionHandlingConfigurer;
import ppl.server.iam.authn.exception.handling.RAccessDeniedHandler;
import ppl.server.iam.authn.exception.handling.RAuthenticationEntryPoint;
import ppl.server.iam.authn.login.*;
import ppl.server.iam.authn.authentication.AuthenticationProviderConfigurer;
import ppl.server.iam.authn.authentication.password.BcryptClientPasswordEncoder;
import ppl.server.iam.authn.authentication.userdetails.LockableUserDetailsService;
import ppl.server.iam.authn.authentication.userdetails.PostAuthenticationChecks;
import ppl.server.iam.authn.authentication.userdetails.PreAuthenticationChecks;
import ppl.server.iam.authn.login.reader.EncryptedUUPATReader;
import ppl.server.iam.authn.ticket.TicketConfigurer;

import java.util.List;

@Configuration
//@AutoConfiguration
public class SecurityConfiguration {
    private static final String DEFAULT_LOGIN_PROCESSING_URL = "/cas/login";
    //    @Bean
//    SecurityFilterChain web(HttpSecurity http) throws Exception {
//        // @formatter:off
//        http
//                .securityContext(context -> context
//                        .securityContextRepository(securityContextRepository())
//                )
//                .authorizeHttpRequests((authorize) -> authorize
//                        .anyRequest().authenticated()
//                ) // 资源授权检查
//                .exceptionHandling((exceptions) -> exceptions
//                        .withObjectPostProcessor(new ObjectPostProcessor<ExceptionTranslationFilter>() {
//                            @Override
//                            public <O extends ExceptionTranslationFilter> O postProcess(O filter) {
//                                filter.setAuthenticationTrustResolver(new MfaTrustResolver());
//                                return filter;
//                            }
//                        })
//                ); // 异常处理
//        // @formatter:on
//        return http.build();
//    }
    @Value("${application.web.csrf.ignore}")
    private List<String> ignoreCsrfProtection;

    @Value("${bcrypt.version:2y}")
    private String version;

    @Value("${bcrypt.log-rounds:10}")
    private int logRounds;

    @Autowired
    private RedisSemaphore loginSemaphore;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.
                apply(new AuthenticationProviderConfigurer<>())
                .userDetailsService(new LockableUserDetailsService())
                .preAuthenticationChecks(new PreAuthenticationChecks(loginSemaphore))
                .postAuthenticationChecks(new PostAuthenticationChecks())
                .clientSaltPublisher(new BcryptClientPasswordEncoder.BcryptClientSaltPublisher(version, logRounds))
        .and().
                apply(new EnhancedLoginConfigurer<>())
                .disableDefaultLoginPage()
                .loginProcessingUrl(DEFAULT_LOGIN_PROCESSING_URL)
                .tokenReader(new EncryptedUUPATReader<>())
                .postProcessedSuccessHandler(new AjaxAuthenticationSuccessHandler())
                .postProcessedFailureHandler(new AjaxAuthenticationFailureHandler())
        .and().
                apply(new EnhancedExceptionHandlingConfigurer<>())
                .authenticationEntryPoint(new RAuthenticationEntryPoint())
                .accessDeniedHandler(new RAccessDeniedHandler())
        .and()
                .apply(new TicketConfigurer<>())
        .and()
                .apply(new ExceptionFallbackConfigurer<>())
        .and()
                .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                .csrf(c -> {
                    RequestMatcher[] ignoreCsrfRequestMatchers = ignoreCsrfProtection.stream()
                            .map(this::antMatcher)
                            .toArray(AntPathRequestMatcher[]::new);
                    c.ignoringRequestMatchers(ignoreCsrfRequestMatchers);
                }).build();
    }

    private static final String ANT_METHOD_PATH_SEPARATOR = "$";

    private AntPathRequestMatcher antMatcher(String matcher) {
        if (matcher.startsWith(ANT_METHOD_PATH_SEPARATOR)) {
            int idx = matcher.indexOf(ANT_METHOD_PATH_SEPARATOR, 1);
            if (idx < 0) {
                throw new IllegalArgumentException("Ant matcher config error. Use {path} or ${method}${path}");
            }
            String method = matcher.substring(1, idx);
            String path = matcher.substring(idx + 1);
            return new AntPathRequestMatcher(path, method);
        } else {
            return new AntPathRequestMatcher(matcher);
        }
    }

//    @Bean
//    SecurityContextRepository securityContextRepository() {
//
//    }
}
