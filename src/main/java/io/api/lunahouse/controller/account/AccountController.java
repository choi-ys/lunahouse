package io.api.lunahouse.controller.account;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    /**
     * 회원 가입 화면 Controller
     * @param model
     * @return
     */
    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

}
