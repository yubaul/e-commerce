package kr.baul.server.domain.order;

import java.util.List;

public interface ItemStockProcessor {
    void deduct(List<OrderCommand.RegisterOrder.OrderItem> items);

}
