package kr.baul.server.domain.order;


import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemStockProcessorImpl implements ItemStockProcessor {
    private final ItemReader itemReader;
    @Override
    public void deduct(List<OrderCommand.RegisterOrder.OrderItem> items) {
        for (OrderCommand.RegisterOrder.OrderItem item : items) {
            Item getItem =  itemReader.getItem(item.itemId());
            getItem.decreaseQuantity(item.quantity());
        }
    }
}
