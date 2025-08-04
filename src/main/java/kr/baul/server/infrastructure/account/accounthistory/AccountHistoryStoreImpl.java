package kr.baul.server.infrastructure.account.accounthistory;

import kr.baul.server.db.AccountHistoryDB;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.accounthistory.AccountHistory;
import kr.baul.server.domain.account.accounthistory.AccountHistoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountHistoryStoreImpl implements AccountHistoryStore {

    private final AccountHistoryRepository accountHistoryRepository;


    @Override
    public AccountHistory store(
            Account account,
            Long amount,
            AccountHistory.TransactionType transactionType,
            AccountHistory.SourceType sourceType,
            Long paymentId
    ) {
        AccountHistory accountHistory = AccountHistory.builder()
                .accountId(account.getId())
                .amount(amount)
                .balance(account.getBalance())
                .transactionType(transactionType)
                .sourceType(sourceType)
                .paymentId(paymentId)
                .build();
        return accountHistoryRepository.save(accountHistory);
    }
}
