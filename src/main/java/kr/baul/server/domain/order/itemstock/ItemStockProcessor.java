package kr.baul.server.domain.order.itemstock;

import kr.baul.server.domain.order.OrderCommand;

import java.util.List;

public interface ItemStockProcessor {
    void deductAllOrRollback(List<OrderCommand.RegisterOrder.OrderItem> items);

    void revertAll(List<OrderCommand.RegisterOrder.OrderItem> items);
}
