package kr.baul.server.domain.order.itemstock;

import kr.baul.server.domain.order.OrderCommand;

public interface ItemStockDeductionHandler {

    void deductOne(OrderCommand.RegisterOrder.OrderItem item);

    void revertOne(OrderCommand.RegisterOrder.OrderItem item);

}
