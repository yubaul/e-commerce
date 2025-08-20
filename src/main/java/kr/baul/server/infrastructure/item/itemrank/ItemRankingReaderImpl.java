package kr.baul.server.infrastructure.item.itemrank;

import kr.baul.server.domain.item.iteminfo.ItemInfo;
import kr.baul.server.domain.item.itemrank.ItemRankingReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ItemRankingReaderImpl implements ItemRankingReader {

    private final StringRedisTemplate redis;

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DAILY_KEY_PREFIX = "cash:item:rank:";
    private static final String UNION_CACHE_KEY = "cash:item:topSelling:last3days";
    private static final long CACHE_TTL_MINUTES = 5;

    @Override
    public List<ItemInfo.TopSelling> getTop5ItemsLast3Days() {
        LocalDate today = LocalDate.now();

        String k0 = DAILY_KEY_PREFIX + DAY_FMT.format(today);
        String k1 = DAILY_KEY_PREFIX + DAY_FMT.format(today.minusDays(1));
        String k2 = DAILY_KEY_PREFIX + DAY_FMT.format(today.minusDays(2));

        ZSetOperations<String, String> z = redis.opsForZSet();

        if (Boolean.TRUE.equals(redis.hasKey(UNION_CACHE_KEY))) {
            return top5(z, UNION_CACHE_KEY);
        }

        z.unionAndStore(k0, List.of(k1, k2), UNION_CACHE_KEY);
        redis.expire(UNION_CACHE_KEY, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        return top5(z, UNION_CACHE_KEY);
    }

    private List<ItemInfo.TopSelling> top5(ZSetOperations<String, String> z, String key) {
        Set<ZSetOperations.TypedTuple<String>> rows = z.reverseRangeWithScores(key, 0, 4);
        if (rows == null || rows.isEmpty()) return List.of();

        return rows.stream()
                .map(t -> {
                    String raw = t.getValue();
                    if (raw == null || raw.isBlank()) return null;

                    long id = Long.parseLong(raw);
                    int salesVolume = t.getScore() == null ? 0 : t.getScore().intValue();

                    return ItemInfo.TopSelling.builder()
                            .itemId(id)
                            .salesVolume(salesVolume)
                            .build();
                })
                .toList();
    }

}
