package kr.baul.server.domain.order.payment;

import kr.baul.server.common.config.CommonLock;
import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.account.AccountReader;
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
    private final AccountReader accountReader;

    @CommonLock(key = "account", id = "#userId")
    @Override
    public void process(Long userId, Order order) {
        Account account = accountReader.getAccount(userId);
        account.use(order.getTotalAmount());
        Payment payment = paymentStore.store(order.getId(), Payment.PayMethod.ACCOUNT, order.getTotalAmount());
        accountHistoryStore.store(
                account,
                order.getTotalAmount(),
                AccountHistory.TransactionType.USE,
                AccountHistory.SourceType.PAY,
                payment.getId()
        );

    }
}
