package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.Profile;
import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AccountService {

    Account processNewAccount(SignUpForm signUpForm);

    void sendSignUpConfirmEmail(Account createdAccount);

    void login(Account account);

    void completeSignUp(Account account);

    void updateProfile(Account account, Profile profile);

    public UserDetails loadUserByUsername(String emailOrEngName) throws UsernameNotFoundException;
}
