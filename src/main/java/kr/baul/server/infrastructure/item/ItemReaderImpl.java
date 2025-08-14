package kr.baul.server.infrastructure.item;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.iteminfo.ItemInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemReaderImpl implements ItemReader {

    private final ItemRepository itemRepository;
    private final ItemQueryDslRepository queryDslRepository;

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 상품이 존재하지 않습니다."));

    }

    @Override
    public List<ItemInfo.TopSelling> getTopSellingItems(LocalDateTime start, LocalDateTime end, int limit) {
        return queryDslRepository.findTopSellingByPeriod(start, end, limit);
    }
}
