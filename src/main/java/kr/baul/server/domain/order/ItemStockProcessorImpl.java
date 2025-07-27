package kr.baul.server.domain.order;


import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.ItemStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemStockProcessorImpl implements ItemStockProcessor {
    private final ItemReader itemReader;
    private final ItemStore itemStore;
    @Override
    public void deduct(List<OrderCommand.RegisterOrder.OrderItem> items) {
        for (OrderCommand.RegisterOrder.OrderItem item : items) {
            Item getItem =  itemReader.getItem(item.itemId());
            getItem.decreaseQuantity(item.quantity());
            itemStore.store(getItem);
        }
    }

    @Override
    public void restore(List<OrderCommand.RegisterOrder.OrderItem> orderItems) {
        for (OrderCommand.RegisterOrder.OrderItem item : orderItems) {
            Item getItem = itemReader.getItem(item.itemId());
            getItem.increaseQuantity(item.quantity());
            itemStore.store(getItem);
        }
    }
}
