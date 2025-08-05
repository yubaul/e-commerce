package kr.baul.server.infrastructure.account;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountReaderImpl implements AccountReader {

    private final AccountRepository accountRepository;

    @Override
    public Account getAccount(Long userId) {
        return accountRepository.findAccountByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자에게 등록된 계좌가 없습니다."));
    }

    @Override
    public Account getAccountWithLock(Long userId) {
        return accountRepository.findForUpdateByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자에게 등록된 계좌가 없습니다."));
    }

}
