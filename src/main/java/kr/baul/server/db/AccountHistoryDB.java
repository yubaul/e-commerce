package kr.baul.server.db;

import kr.baul.server.domain.account.accounthistory.AccountHistory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AccountHistoryDB {
    private Map<Long, AccountHistory> table = new HashMap<>();
    private static AtomicInteger integer = new AtomicInteger(1);

    public AccountHistory insert(AccountHistory accountHistory) {
        throttle(300);
        table.put(accountHistory.getId(), accountHistory);
        return accountHistory;
    }

    public static long getAtomicInteger(){
        return integer.getAndIncrement();
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }

}
