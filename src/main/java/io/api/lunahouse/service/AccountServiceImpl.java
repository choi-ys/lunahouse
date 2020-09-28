package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @Override
    public Account processNewAccount(SignUpForm signUpForm) {
        Account createdAccount = this.saveNewAccount(signUpForm);

        createdAccount.generateEmailToken();
        this.sendSignUpConfirmEmail(createdAccount);
        return createdAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .engName(signUpForm.getEngName())
                .password(signUpForm.getPassword())
                .emailVerified(false)
                .eventCreatedByWeb(true)
                .eventEnrollmentResultByWeb(true)
                .eventUpdatedResultByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account createdAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(createdAccount.getEmail());
        simpleMailMessage.setSubject("[루나하우스] 회원 가입 인증 메일입니다.");
        simpleMailMessage.setText("/check-email-token?token="+createdAccount.getEmailCheckToken() +"&email="+createdAccount.getEmail());

        javaMailSender.send(simpleMailMessage);
    }
}
