package kr.baul.server.domain.order.payment;

public interface PaymentStore {

    Payment store(Long orderId, Payment.PayMethod payMethod, Long payAmount);
}
