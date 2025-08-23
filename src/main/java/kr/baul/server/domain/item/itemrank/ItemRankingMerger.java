package kr.baul.server.domain.item.itemrank;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.iteminfo.ItemInfo;

import java.util.List;

public interface ItemRankingMerger {
    List<ItemInfo.TopSelling> merge(List<ItemInfo.TopSelling> ranked, List<Item> items);
}
