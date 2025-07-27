package kr.baul.server.domain.order;

import java.util.List;

public interface OrderFactory {

    Order store(Long userId, List<OrderCommand.RegisterOrder.OrderItem> items);
}
