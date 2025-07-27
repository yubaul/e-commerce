package kr.baul.server.db;

import kr.baul.server.domain.order.payment.Payment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PaymentDB {

    private Map<Long, Payment> table = new HashMap<>();

    private static AtomicInteger integer = new AtomicInteger(1);

    public Payment insert(Payment payment){
        throttle(300);
        table.put(payment.getId(), payment);
        return payment;
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
