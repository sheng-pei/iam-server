package ppl.server.iam.authn.autoconfiguration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.iam.authn.authentication.userdetails.UserLockedException;
import ppl.server.iam.authn.login.AlreadyAuthenticatedException;
import ppl.server.iam.authn.login.InvalidAuthenticationRequestException;
import ppl.server.iam.authn.ticket.InvalidTicketException;
import ppl.server.iam.authn.ticket.TooManyTicketGeneratedException;

@Configuration
//@AutoConfiguration
public class RConfiguration implements InitializingBean {
    @Autowired
    private Rcs rcs;

    @Override
    public void afterPropertiesSet() throws Exception {
        //user error
        rcs.register(10001, "用户名或密码错误。",
                UsernameNotFoundException.class,
                BadCredentialsException.class,
                ProviderNotFoundException.class,
                DisabledException.class,
                AccountExpiredException.class,
                CredentialsExpiredException.class);
        rcs.register(10002, "账号已被锁定, 请在{}分钟后重试。", UserLockedException.class);
        rcs.register(10003, "不合法登录请求。", InvalidAuthenticationRequestException.class);
        rcs.register(10004, "未登录。", InsufficientAuthenticationException.class);
        rcs.register(10005, "请求不合法。", InvalidCsrfTokenException.class);
        rcs.register(10006, "用户已登录，重复认证。", AlreadyAuthenticatedException.class);
        rcs.register(10007, "票据获取过于频繁，请在{}分钟后重试。", TooManyTicketGeneratedException.class);
        rcs.register(10008, "票据不合法。", InvalidTicketException.class);
    }
}
