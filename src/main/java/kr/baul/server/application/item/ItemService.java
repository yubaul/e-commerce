package kr.baul.server.application.item;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemReader itemReader;

    @Transactional(readOnly = true)
    public ItemInfo getItem(Long itemId){
        Item item = itemReader.getItem(itemId);
        return new ItemInfo(item);
    }

}
