package io.api.lunahouse.controller.account;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.dto.SignUpFormValidator;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.repository.AccountRepository;
import io.api.lunahouse.service.AccountServiceImpl;
import io.api.lunahouse.util.annotation.CurrentUser;
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

        Account createdAccount = accountService.processNewAccount(signUpForm);
        accountService.login(createdAccount);

        // 회원가입 완료 시
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if(null == account){
            model.addAttribute("error", "not exist email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong toekn");
            return view;
        }

        account.completeSignUp();
        accountService.login(account);

        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("engName", account.getEngName());
        return view;
    }

    @GetMapping("check-email-auth")
    public String checkEmail(@CurrentUser Account account, Model model){
        String view = "account/check-email-auth";

        if(null == account){
            model.addAttribute("error", "not exist email");
            return view;
        }

        model.addAttribute(account);
        model.addAttribute("engName", account.getEngName());
        model.addAttribute("email", account.getEmail());
        return view;
    }

    @GetMapping("resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model){
        String view = "account/check-email-auth";

        if(null == account){
            model.addAttribute("error", "유효하지 못한 사용자의 요청입니다.");
            return view;
        }

        model.addAttribute(account);
        if(!account.canSendConfirmEmail()){
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("canSendConfirmEmailDateTime", account.getEmailCheckToeknGeneratedAt().plusHours(1));
            return view;
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";

    }

}
