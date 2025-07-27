package kr.baul.server.db;

import kr.baul.server.domain.order.orderitem.OrderItem;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderItemDB {

    private Map<Long, OrderItem> table = new HashMap<>();

    private static AtomicInteger integer = new AtomicInteger(1);

    public OrderItem insert(OrderItem orderItem){
        throttle(300);
        table.put(orderItem.getId(), orderItem);
        return orderItem;
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
