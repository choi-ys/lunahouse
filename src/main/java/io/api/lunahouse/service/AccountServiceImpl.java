package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.domain.account.entity.UserAccount;
import io.api.lunahouse.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account createdAccount = this.saveNewAccount(signUpForm);

        this.sendSignUpConfirmEmail(createdAccount);
        return createdAccount;
    }

    @Override
    public void sendSignUpConfirmEmail(Account createdAccount) {
        createdAccount.generateEmailToken();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(createdAccount.getEmail());
        simpleMailMessage.setSubject("[루나하우스] 회원 가입 인증 메일입니다.");
        simpleMailMessage.setText("/check-email-token?token="+createdAccount.getEmailCheckToken() +"&email="+createdAccount.getEmail());

        javaMailSender.send(simpleMailMessage);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .engName(signUpForm.getEngName())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .emailVerified(false)
                .eventCreatedByWeb(true)
                .eventEnrollmentResultByWeb(true)
                .eventUpdatedResultByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    @Override
    public void login(Account createdAccount) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(createdAccount),
                createdAccount.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrEngName) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrEngName);
        if(account == null){
            account = accountRepository.findByEngName(emailOrEngName);
        }

        if(account == null){
            throw new UsernameNotFoundException(emailOrEngName);
        }

        return new UserAccount(account);
    }
}
