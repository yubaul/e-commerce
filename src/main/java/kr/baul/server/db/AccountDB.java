package kr.baul.server.db;

import jakarta.annotation.PostConstruct;
import kr.baul.server.domain.account.Account;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class AccountDB {

    private final Map<Long, Account> table = new HashMap<>();

    @PostConstruct
    public void init(){
        var account = Account.builder()
                .userId(1L)
                .balance(0L)
                .build();
        table.put(account.getUserId(), account);
    }

    public Optional<Account> selectById(Long userId) {
        throttle(200);
        return Optional.ofNullable(table.get(userId));
    }

    public Account insertOrUpdate(Account account) {
        throttle(300);
        return table.put(account.getUserId(), account);
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }


}
