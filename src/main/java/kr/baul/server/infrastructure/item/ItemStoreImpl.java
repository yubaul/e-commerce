package kr.baul.server.infrastructure.item;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStoreImpl implements ItemStore {

    private final ItemRepository itemRepository;

    @Override
    public Item store(Item item) {
        return itemRepository.save(item);
    }
}
