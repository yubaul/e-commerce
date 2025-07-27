package kr.baul.server.infrastructure.order.pay;

import kr.baul.server.db.PaymentDB;
import kr.baul.server.domain.order.payment.Payment;
import kr.baul.server.domain.order.payment.PaymentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStoreImpl implements PaymentStore {
    private final PaymentDB paymentDB;
    @Override
    public Payment store(Long orderId, Long accountHistoryId, Payment.PayMethod payMethod, Long payAmount) {
        Payment payment = Payment.builder()
                .id(PaymentDB.getAtomicInteger())
                .orderId(orderId)
                .accountHistoryId(accountHistoryId)
                .payMethod(payMethod)
                .payAmount(payAmount)
                .build();
        return paymentDB.insert(payment);
    }
}
