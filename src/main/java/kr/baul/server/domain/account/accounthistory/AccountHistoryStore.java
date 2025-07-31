package kr.baul.server.domain.account.accounthistory;

import kr.baul.server.domain.account.Account;

public interface AccountHistoryStore {

    AccountHistory store(Account account,
                         Long amount,
                         AccountHistory.TransactionType transactionType,
                         AccountHistory.SourceType sourceType,
                         Long paymentId
                         );

}
