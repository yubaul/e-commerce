package kr.baul.server.domain.order.itemstock;

import kr.baul.server.common.config.CommonLock;
import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.item.ItemStockReader;
import kr.baul.server.domain.item.ItemStockStore;
import kr.baul.server.domain.order.OrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStockDeductionHandlerImpl implements ItemStockDeductionHandler{

    private final ItemStockReader itemStockReader;
    private final ItemStockStore itemStockStore;

    @CommonLock(key = "itemStock", id = "#item.itemId()")
    @Override
    public void deductOne(OrderCommand.RegisterOrder.OrderItem item) {
        ItemStock stock = itemStockReader.getItemStock(item.itemId());
        stock.decrease(item.quantity());
        itemStockStore.store(stock);
    }

    @CommonLock(key = "itemStock", id = "#item.itemId()")
    @Override
    public void revertOne(OrderCommand.RegisterOrder.OrderItem item) {
        ItemStock stock = itemStockReader.getItemStock(item.itemId());
        stock.increase(item.quantity());
        itemStockStore.store(stock);
    }
}
