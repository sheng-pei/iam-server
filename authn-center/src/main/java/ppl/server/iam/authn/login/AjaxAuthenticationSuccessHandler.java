package ppl.server.iam.authn.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ppl.server.base.webmvc.response.r.Rcs;
import ppl.server.base.webmvc.response.writer.Writers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//TODO,登录成功并跳转
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Writers writers;
    private Rcs rcs;

    @Autowired
    public void setWriters(Writers writers) {
        this.writers = writers;
    }

    @Autowired
    public void setRcs(Rcs rcs) {
        this.rcs = rcs;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        writers.http(response)
                .json()
                .write(rcs.ok().message("登录成功"));
    }
}
