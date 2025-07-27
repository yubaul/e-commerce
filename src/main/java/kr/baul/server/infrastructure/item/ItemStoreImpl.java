package kr.baul.server.infrastructure.item;

import kr.baul.server.db.ItemDB;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStoreImpl implements ItemStore {
    private final ItemDB itemDB;
    @Override
    public Item store(Item item) {
        return itemDB.insert(item);
    }
}
