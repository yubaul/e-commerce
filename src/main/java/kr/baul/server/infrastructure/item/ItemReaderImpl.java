package kr.baul.server.infrastructure.item;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.db.ItemDB;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemReaderImpl implements ItemReader {

    private final ItemDB itemDB;

    @Override
    public Item getItem(Long itemId) {
        return itemDB.selectById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 상품이 존재하지 않습니다."));
    }
}
