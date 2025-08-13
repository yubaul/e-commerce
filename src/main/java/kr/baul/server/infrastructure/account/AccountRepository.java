package kr.baul.server.infrastructure.account;

import kr.baul.server.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByUserId(Long userId);

}
