package kr.baul.server.infrastructure.account;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountStoreImpl implements AccountStore {

    private final AccountRepository accountRepository;

    @Override
    public Account store(Account account) {
        return accountRepository.save(account);
    }
}
