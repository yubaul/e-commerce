package kr.baul.server.domain.order.payment;

import kr.baul.server.domain.order.Order;

public interface PaymentProcessor {

    void process(Long userId, Order order);

}
