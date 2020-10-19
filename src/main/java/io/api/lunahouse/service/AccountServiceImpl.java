package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.settings.Notifications;
import io.api.lunahouse.domain.account.dto.settings.Profile;
import io.api.lunahouse.domain.account.dto.account.SignUpForm;
import io.api.lunahouse.domain.account.entity.account.Account;
import io.api.lunahouse.domain.account.entity.account.UserAccount;
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
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements UserDetailsService, AccountService{

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
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
                .eventUpdatedByWeb(true)
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
    public void completeSignUp(Account account) {
        account.completeSignUp();
        this.login(account);
    }

    @Transactional(readOnly = true)
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

    @Override
    public void updateProfile(Account account, Profile profile) {
        account.setStatusMessage(profile.getStatusMessage());
        account.setUrl(profile.getUrl());
        account.setTeam(profile.getTeam());
        account.setLocation(profile.getLocation());
        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }

    @Override
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    @Override
    public void updateNotifications(Account account, Notifications notifications) {
        account.setEventCreatedByWeb(notifications.isEventCreatedByWeb());
        account.setEventCreatedByEmail(notifications.isEventCreatedByEmail());
        account.setEventUpdatedByWeb(notifications.isEventUpdatedByWeb());
        account.setEventUpdatedByEmail(notifications.isEventUpdatedByEmail());
        account.setEventEnrollmentResultByEmail(notifications.isEventEnrollmentResultByEmail());
        account.setEventEnrollmentResultByWeb(notifications.isEventEnrollmentResultByWeb());
        accountRepository.save(account);
    }
}
