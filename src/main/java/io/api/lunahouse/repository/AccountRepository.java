package io.api.lunahouse.repository;

import io.api.lunahouse.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByEngName(String engName);

    Account findByEmail(String email);
}
