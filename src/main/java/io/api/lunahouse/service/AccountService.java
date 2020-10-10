package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AccountService extends UserDetailsService {

    Account processNewAccount(SignUpForm signUpForm);

    void sendSignUpConfirmEmail(Account createdAccount);

    void login(Account account);

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;

    void completeSignUp(Account account);
}
