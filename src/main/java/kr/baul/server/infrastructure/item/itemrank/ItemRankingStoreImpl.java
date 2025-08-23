package kr.baul.server.infrastructure.item.itemrank;

import kr.baul.server.domain.item.itemrank.ItemRankingStore;
import kr.baul.server.domain.order.OrderCommand;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ItemRankingStoreImpl implements ItemRankingStore {

    private final RedissonClient redisson;
    private final int EXPIRE_DAY = 4;
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void updateDailyRanking(List<OrderCommand.RegisterOrder.OrderItem> items) {
        if (items == null || items.isEmpty()) return;

        String today = DAY_FMT.format(LocalDate.now());
        String key = "cash:item:rank:" + today;

        RScoredSortedSet<Long> zset = redisson.getScoredSortedSet(key);

        for (OrderCommand.RegisterOrder.OrderItem item : items) {
            if (item.itemId() == null || item.quantity() == null) continue;
            zset.addScore(item.itemId(), item.quantity());
        }

        if (zset.getExpireTime() == -1) {
            redisson.getKeys().expire(key, EXPIRE_DAY, TimeUnit.DAYS);
        }
    }
}
