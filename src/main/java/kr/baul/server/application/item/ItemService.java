package kr.baul.server.application.item;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.domain.item.ItemReader;
import kr.baul.server.domain.item.iteminfo.ItemInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemReader itemReader;
    private final ItemInfoMapper itemInfoMapper;


    @Transactional(readOnly = true)
    public ItemInfo.Item getItem(Long itemId){
        Item item = itemReader.getItem(itemId);
        return itemInfoMapper.of(item);
    }

    public List<ItemInfo.TopSelling> getTopSellingItems(){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(2).atStartOfDay();
        LocalDateTime endExclusive = today.plusDays(1).atStartOfDay();
        return itemReader.getTopSellingItems(start, endExclusive, 5);
    }

}
