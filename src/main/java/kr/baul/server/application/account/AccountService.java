package kr.baul.server.application.account;

import static kr.baul.server.interfaces.account.AccountDto.AccountChargeRequest;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import kr.baul.server.domain.account.AccountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReader accountReader;
    private final AccountStore accountStore;


    public Long charge(AccountChargeRequest request){
        Account account = accountReader.getAccount(request.getUserId());
        account.charge(request.getAmount());
        return accountStore.store(account).getBalance();
    }

    public Long getAccountBalance(Long userId){
        return accountReader.getAccount(userId).getBalance();
    }

}
