package kr.baul.server.infrastructure.account.accounthistory;

import kr.baul.server.domain.account.accounthistory.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
}
