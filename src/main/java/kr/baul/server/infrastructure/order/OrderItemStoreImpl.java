package kr.baul.server.infrastructure.order;

import kr.baul.server.db.OrderItemDB;
import kr.baul.server.domain.order.orderitem.OrderItem;
import kr.baul.server.domain.order.orderitem.OrderItemStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemStoreImpl implements OrderItemStore {

    private final OrderItemDB orderItemDB;

    @Override
    public List<OrderItem> store(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItemDB::insert)
                .toList();
    }
}
