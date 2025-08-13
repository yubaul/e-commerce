package kr.baul.server.application.account;

import static kr.baul.server.domain.account.accounthistory.AccountHistory.*;

import kr.baul.server.common.config.CommonLock;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountCommand;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.account.AccountStore;
import kr.baul.server.domain.account.accounthistory.AccountHistoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReader accountReader;
    private final AccountStore accountStore;
    private final AccountHistoryStore accountHistoryStore;


    @CommonLock(key = "account", id = "#command.userId()")
    public Long charge(AccountCommand.AccountCharge command){
        var amount = command.amount();
        Account account = accountReader.getAccount(command.userId());
        account.charge(amount);

        accountStore.store(account);
        accountHistoryStore.store(account, amount, TransactionType.CHARGE, SourceType.MANUAL, null);
        return account.getBalance();
    }

    @Transactional(readOnly = true)
    public Long getAccountBalance(Long userId){
        return accountReader.getAccount(userId).getBalance();
    }

}
