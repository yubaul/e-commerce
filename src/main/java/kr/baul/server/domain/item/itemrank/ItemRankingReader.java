package kr.baul.server.domain.item.itemrank;

import kr.baul.server.domain.item.iteminfo.ItemInfo;

import java.util.List;

public interface ItemRankingReader {
    List<ItemInfo.TopSelling> getTop5ItemsLast3Days();
}
