package io.api.lunahouse.controller.login;

import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.util.annotation.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@CurrentUser Account account){
        if(account != null){
            return "redirect:/";
        }

        String view = "login";
        return view;
    }

}
