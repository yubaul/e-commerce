package kr.baul.server.domain.order.payment;

public interface PaymentStore {

    Payment store(Long orderId, Long accountHistoryId, Payment.PayMethod payMethod, Long payAmount);
}
