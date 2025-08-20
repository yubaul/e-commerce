package kr.baul.server.domain.item.itemrank;

import kr.baul.server.domain.item.Item;
import kr.baul.server.domain.item.iteminfo.ItemInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRankingMergerImpl implements ItemRankingMerger{
    @Override
    public List<ItemInfo.TopSelling> merge(List<ItemInfo.TopSelling> ranked, List<Item> items) {
        if (ranked == null || ranked.isEmpty()) return List.of();

        Map<Long, Item> byId = items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        return ranked.stream()
                .map(r -> {
                    Item it = byId.get(r.getItemId());
                    String name = (it != null) ? it.getName() : null;
                    return ItemInfo.TopSelling.builder()
                            .itemId(r.getItemId())
                            .itemName(name)
                            .salesVolume(r.getSalesVolume())
                            .build();
                })
                .toList();
    }
}
