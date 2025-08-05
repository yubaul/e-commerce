package kr.baul.server.domain.account;

public interface AccountReader {
     Account getAccount(Long userId);

     Account getAccountWithLock(Long userId);
}
