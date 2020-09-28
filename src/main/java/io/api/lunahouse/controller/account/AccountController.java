package io.api.lunahouse.controller.account;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.dto.SignUpFormValidator;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

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

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .engName(signUpForm.getEngName())
                .password(signUpForm.getPassword())
                .emailVerified(false)
                .eventCreatedByWeb(true)
                .eventEnrollmentResultByWeb(true)
                .eventUpdatedResultByWeb(true)
                .build();

        Account createdAccount = accountRepository.save(account);

        signUpFormValidator.validate(signUpForm, errors);
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        createdAccount.generateEmailToken();
        String receiverMail = createdAccount.getEmail();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(receiverMail);
        simpleMailMessage.setSubject("[루나하우스] 회원 가입 인증 메일입니다.");
        simpleMailMessage.setText("/check-email-token?token="+createdAccount.getEmailCheckToken() +"&email="+receiverMail);

        javaMailSender.send(simpleMailMessage);

        // 회원가입 완료 시
        return "redirect:/";
    }

}
