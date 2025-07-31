package kr.baul.server.infrastructure.order.pay;

import kr.baul.server.domain.order.payment.Payment;
import kr.baul.server.domain.order.payment.PaymentRepository;
import kr.baul.server.domain.order.payment.PaymentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStoreImpl implements PaymentStore {
    private final PaymentRepository paymentRepository;
    @Override
    public Payment store(Long orderId, Payment.PayMethod payMethod, Long payAmount) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .payMethod(payMethod)
                .payAmount(payAmount)
                .build();
        return paymentRepository.save(payment);
    }
}
