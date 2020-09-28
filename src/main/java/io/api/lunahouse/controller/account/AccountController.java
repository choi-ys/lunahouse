package io.api.lunahouse.controller.account;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.dto.SignUpFormValidator;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountServiceImpl accountService;

//    @InitBinder("signUpForm")
//    public void initBinder(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(signUpFormValidator);
//    }

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

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        signUpFormValidator.validate(signUpForm, errors);
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        accountService.processNewAccount(signUpForm);

        // 회원가입 완료 시
        return "redirect:/";
    }

}
