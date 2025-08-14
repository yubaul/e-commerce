package kr.baul.server.domain.item;

import kr.baul.server.domain.item.iteminfo.ItemInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemReader {
    Item getItem(Long itemId);

    List<ItemInfo.TopSelling> getTopSellingItems(LocalDateTime start,
                                                 LocalDateTime end,
                                                 int limit);
}