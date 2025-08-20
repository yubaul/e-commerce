package kr.baul.server;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@Profile({"local","test"})
@RequiredArgsConstructor
public class ItemRankingSeed {

    private final RedissonClient redisson;

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DAILY_KEY_PREFIX = "cash:item:rank:";
    private static final int ITEMS_PER_DAY = 20;
    private static final long DAILY_TTL_DAYS = 4;
    private static final long BASE_ITEM_ID = 2300;
    private static final String LIMIT_BUCKET_KEY = "coupon:issue:queue:limit:";

    @PostConstruct
    public void seed() {
        purgeSeedKeys();

        LocalDate today = LocalDate.now();
        seedOneDay(today);
        seedOneDay(today.minusDays(1));
        seedOneDay(today.minusDays(2));

        // 쿠폰 한정 수량 Redis에 미리 세팅
        seedCouponLimits();
    }

    private void purgeSeedKeys() {
        var keys = redisson.getKeys();
        // Item 랭킹
        keys.deleteByPattern("cash:item:rank:*");
        // Coupon issuing 관련
        keys.deleteByPattern("coupon:issue:queue:limit:*");
        keys.deleteByPattern("coupon:issue:queue:seq:*");
        keys.deleteByPattern("coupon:issue:queue:seen:*");
        keys.deleteByPattern("coupon:issue:queue:*");
        keys.deleteByPattern("coupon:issued:*");
    }

    private void seedOneDay(LocalDate day) {
        String key = DAILY_KEY_PREFIX + DAY_FMT.format(day);

        RScoredSortedSet<String> zset = redisson.getScoredSortedSet(key);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < ITEMS_PER_DAY; i++) {
            String member = String.valueOf(BASE_ITEM_ID + i);
            double score = rnd.nextInt(1, 201);
            zset.add(score, member);
        }

        redisson.getKeys().expire(key, DAILY_TTL_DAYS, TimeUnit.DAYS);
    }

    private String kLimit(Long couponId) {
        return LIMIT_BUCKET_KEY + couponId;
    }

    private RBucket<Integer> limitBucket(Long couponId) {
        return redisson.getBucket(kLimit(couponId));
    }

    private void seedCouponLimits() {
        limitBucket(1L).set(2, 3, TimeUnit.MINUTES);
        limitBucket(200L).set(5, 3, TimeUnit.MINUTES);
        limitBucket(201L).set(5, 3, TimeUnit.MINUTES);
        limitBucket(500L).set(30, 3, TimeUnit.MINUTES);
        limitBucket(501L).set(10, 3, TimeUnit.MINUTES);
        limitBucket(502L).set(10, 3, TimeUnit.MINUTES);
    }
}
