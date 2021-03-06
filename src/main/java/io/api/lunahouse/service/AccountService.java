package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.account.SignUpForm;
import io.api.lunahouse.domain.account.dto.settings.Notifications;
import io.api.lunahouse.domain.account.dto.settings.Profile;
import io.api.lunahouse.domain.account.entity.account.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AccountService {

    Account processNewAccount(SignUpForm signUpForm);

    void sendSignUpConfirmEmail(Account createdAccount);

    void login(Account account);

    void completeSignUp(Account account);

    void updateProfile(Account account, Profile profile);

    public UserDetails loadUserByUsername(String emailOrEngName) throws UsernameNotFoundException;

    void updatePassword(Account account, String newPassword);

    void updateNotifications(Account account, Notifications notifications);
}
