package kr.baul.server.infrastructure.account;

import kr.baul.server.db.AccountDB;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountStoreImpl implements AccountStore {

    private final AccountDB accountDB;

    @Override
    public Account store(Account account) {
        return accountDB.insertOrUpdate(account);
    }
}
