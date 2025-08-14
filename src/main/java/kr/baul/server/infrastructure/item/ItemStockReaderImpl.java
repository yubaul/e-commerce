package kr.baul.server.infrastructure.item;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.item.ItemStock;
import kr.baul.server.domain.item.ItemStockReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStockReaderImpl implements ItemStockReader {

    private final ItemStockRepository itemStockRepository;

    @Override
    public ItemStock getItemStock(Long itemIId) {
        return itemStockRepository.findByItemId(itemIId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 없습니다."));
    }
}
