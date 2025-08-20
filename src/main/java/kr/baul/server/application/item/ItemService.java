package kr.baul.server.application.item;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.iteminfo.ItemInfoMapper;
import kr.baul.server.domain.item.itemrank.ItemRankingMerger;
import kr.baul.server.domain.item.itemrank.ItemRankingReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemReader itemReader;
    private final ItemInfoMapper itemInfoMapper;
    private final ItemRankingReader itemRankingReader;
    private final ItemRankingMerger itemRankingMerger;


    @Transactional(readOnly = true)
    public ItemInfo.Item getItem(Long itemId){
        Item item = itemReader.getItem(itemId);
        return itemInfoMapper.of(item);
    }

    @Transactional(readOnly = true)
    public List<ItemInfo.TopSelling> getTopSellingItems() {
        var ranked = itemRankingReader.getTop5ItemsLast3Days();
        if (ranked == null || ranked.isEmpty()) return List.of();

        var ids = ranked.stream().map(ItemInfo.TopSelling::getItemId).toList();
        var items = itemReader.getItems(ids);

        return itemRankingMerger.merge(ranked, items);
    }

}
