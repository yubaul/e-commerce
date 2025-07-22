package kr.baul.server.application.account;

import static kr.baul.server.interfaces.account.AccountDto.AccountChargeRequest;
import static kr.baul.server.domain.account.accounthistory.AccountHistory.*;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.account.AccountStore;
import kr.baul.server.domain.account.accounthistory.AccountHistoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReader accountReader;
    private final AccountStore accountStore;
    private final AccountHistoryStore accountHistoryStore;


    public Long charge(AccountChargeRequest request){
        var amount = request.getAmount();
        Account account = accountReader.getAccount(request.getUserId());
        account.charge(amount);

        accountStore.store(account);
        accountHistoryStore.store(account, amount, TransactionType.CHARGE, SourceType.MANUAL);
        return account.getBalance();
    }

    public Long getAccountBalance(Long userId){
        return accountReader.getAccount(userId).getBalance();
    }

}
