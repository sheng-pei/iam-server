package ppl.server.iam.data.controller.authn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ppl.server.base.webmvc.response.jackson.JController;
import ppl.server.iam.authn.UserService;
import ppl.server.iam.authn.bo.EnhancedUser;

@JController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getByUsername")
    public EnhancedUser getByUsername(@RequestParam("username") String username) {
        return userService.getByUsername(username);
    }
}
