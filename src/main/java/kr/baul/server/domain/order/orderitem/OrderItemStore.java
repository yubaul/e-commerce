package kr.baul.server.domain.order.orderitem;

import java.util.List;

public interface OrderItemStore {
    List<OrderItem> store(List<OrderItem> orderItems);
}
