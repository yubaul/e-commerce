package kr.baul.server.domain.order;


import kr.baul.server.domain.item.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemStockProcessorImpl implements ItemStockProcessor {
    private final ItemStockReader itemStockReader;
    private final ItemStockStore itemStockStore;
    @Override
    public void deduct(List<OrderCommand.RegisterOrder.OrderItem> items) {
        for (OrderCommand.RegisterOrder.OrderItem item : items) {
            ItemStock itemStock = itemStockReader.getItemStockWithLock(item.itemId());
            itemStock.decrease(item.quantity());
            itemStockStore.store(itemStock);
        }
    }
}
