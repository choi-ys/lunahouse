package io.api.lunahouse.controller.account;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.dto.SignUpFormValidator;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.repository.AccountRepository;
import io.api.lunahouse.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountServiceImpl accountService;
    private final AccountRepository accountRepository;

//    @InitBinder("signUpForm")
//    public void initBinder(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(signUpFormValidator);
//    }

    /**
     * 회원 가입 화면 Controller
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

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checkedEmail";

        if(null == account){
            model.addAttribute("error", "not exist email");
            return view;
        }

        if(!token.equals(account.getEmailCheckToken())){
            model.addAttribute("error", "wrong toekn");
            return view;
        }

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("engName", account.getEngName());
        return view;


    }

}
