package kr.baul.server;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@Profile("local")
@RequiredArgsConstructor
public class ItemRankingSeed {

    private final RedissonClient redisson;

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DAILY_KEY_PREFIX = "cash:item:rank:";
    private static final int ITEMS_PER_DAY = 20;
    private static final long DAILY_TTL_DAYS = 4;
    private static final long BASE_ITEM_ID = 2300;

    @PostConstruct
    public void seed() {
        LocalDate today = LocalDate.now();
        seedOneDay(today);
        seedOneDay(today.minusDays(1));
        seedOneDay(today.minusDays(2));
    }

    private void seedOneDay(LocalDate day) {
        String key = DAILY_KEY_PREFIX + DAY_FMT.format(day);
        redisson.getKeys().delete(key);

        RScoredSortedSet<String> zset = redisson.getScoredSortedSet(key);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < ITEMS_PER_DAY; i++) {
            String member = String.valueOf(BASE_ITEM_ID + i);
            double score = rnd.nextInt(1, 201);
            zset.add(score, member);
        }

        redisson.getKeys().expire(key, DAILY_TTL_DAYS, TimeUnit.DAYS);
    }
}
