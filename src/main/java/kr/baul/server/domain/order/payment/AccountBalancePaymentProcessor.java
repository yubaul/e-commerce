package kr.baul.server.domain.order.payment;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.accounthistory.AccountHistory;
import kr.baul.server.domain.account.accounthistory.AccountHistoryStore;
import kr.baul.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBalancePaymentProcessor implements PaymentProcessor {

    private final AccountHistoryStore accountHistoryStore;
    private final PaymentStore paymentStore;

    @Override
    public void process(Account account, Order order) {
        account.use(order.getTotalAmount());

        AccountHistory accountHistory = accountHistoryStore.store(
                account,
                order.getTotalAmount(),
                AccountHistory.TransactionType.USE,
                AccountHistory.SourceType.PAY
        );

        paymentStore.store(order.getId(), accountHistory.getId(), Payment.PayMethod.ACCOUNT, order.getTotalAmount());
    }
}
