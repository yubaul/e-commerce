package kr.baul.server.domain.order.payment;

import kr.baul.server.domain.account.Account;
import kr.baul.server.domain.order.Order;

public interface PaymentProcessor {

    void process(Account account, Order order);

}
