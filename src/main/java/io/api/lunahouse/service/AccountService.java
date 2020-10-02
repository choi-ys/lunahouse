package io.api.lunahouse.service;

import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.domain.account.entity.Account;

public interface AccountService {

    Account processNewAccount(SignUpForm signUpForm);

    void login(Account account);
}
