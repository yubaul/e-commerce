package kr.baul.server.infrastructure.account;

import jakarta.persistence.LockModeType;
import kr.baul.server.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findForUpdateByUserId(Long userId);
}
