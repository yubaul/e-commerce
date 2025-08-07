package kr.baul.server.infrastructure.item;

import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.item.ItemStockStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStockStoreImpl implements ItemStockStore {

    private final ItemStockRepository itemStockRepository;

    @Override
    public ItemStock store(ItemStock itemStock) {
        return itemStockRepository.save(itemStock);
    }
}
