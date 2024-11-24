package ppl.server.iam.data.controller.authn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ppl.server.iam.authn.UserService;
import ppl.server.iam.authn.bo.EnhancedUser;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getByUsername")
    public EnhancedUser getByUsername(@RequestParam("username") String username) {
        return userService.getByUsername(username);
    }
}
