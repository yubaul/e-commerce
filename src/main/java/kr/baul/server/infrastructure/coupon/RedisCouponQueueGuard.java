package kr.baul.server.infrastructure.coupon;

import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.domain.coupon.issuing.CouponQueueGuard;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCouponQueueGuard implements CouponQueueGuard {

    private final RedissonClient redisson;
    private static final long TTL_DAYS = 3;

    // Key Builders
    private String kIssued(Long couponId)      { return "coupon:issued:" + couponId; }
    private String kSeen(Long couponId)        { return "coupon:issue:queue:seen:" + couponId; }
    private String kQueue(Long couponId)       { return "coupon:issue:queue:" + couponId; }
    private String kLimit(Long couponId)        { return "coupon:issue:queue:limit:" + couponId; }
    private String kSeq(Long couponId)         { return "coupon:issue:queue:seq:" + couponId; }

    private RSet<String> issued(Long couponId) { return redisson.getSet(kIssued(couponId)); }
    private RSet<String> seen(Long couponId)   { return redisson.getSet(kSeen(couponId)); }
    private RScoredSortedSet<String> queue(Long couponId) { return redisson.getScoredSortedSet(kQueue(couponId)); }
    private RBucket<String> limit(Long couponId) { return redisson.getBucket(kLimit(couponId)); }
    private RAtomicLong seq(Long couponId)     { return redisson.getAtomicLong(kSeq(couponId)); }


    private boolean addSeenWithTtl(Long couponId, String userIdString) {
        RSet<String> s = seen(couponId);
        boolean existed = s.isExists();
        boolean first = s.add(userIdString);
        if (!existed && first) {
            s.expire(TTL_DAYS, TimeUnit.DAYS);
        }
        return first;
    }


    private void addQueueWithTtl(Long couponId, long score, String userIdString) {
        RScoredSortedSet<String> q = queue(couponId);
        boolean existed = q.isExists();
        q.add(score, userIdString);
        if (!existed) {
            q.expire(TTL_DAYS, TimeUnit.DAYS);
        }
    }


    private long incrSeqWithTtl(Long couponId) {
        RAtomicLong a = seq(couponId);
        boolean existed = a.isExists();
        long v = a.incrementAndGet(); // 이 시점에 키가 생성됨(없었다면)
        if (!existed) {
            a.expire(TTL_DAYS, TimeUnit.DAYS);
        }
        return v;
    }


    private void addIssuedWithTtl(Long couponId, String userIdString) {
        RSet<String> set = issued(couponId);
        boolean existed = set.isExists();
        boolean added = set.add(userIdString);
        if (!existed && added) {
            set.expire(TTL_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public void assertNotIssued(Long couponId, Long userId) {
        if (issued(couponId).contains(userId.toString())){
            throw new DuplicateCouponIssueException();
        }
    }


    @Override
    public void tryEnter(Long couponId, Long userId) {
        var userIdString = userId.toString();

        // 1) NX 가드
        if (!addSeenWithTtl(couponId, userIdString)) {
            throw new DuplicateCouponIssueException();
        }

        // 2) 순번 증가 + 대기열 진입
        long score = incrSeqWithTtl(couponId);
        addQueueWithTtl(couponId, score, userIdString);

        // 3) 정원 초과 보정
        int limit = Integer.parseInt(limit(couponId).get().trim());
        RScoredSortedSet<String> q = queue(couponId);
        while (q.size() > limit) {
            String removed = q.pollLast();
            if (removed == null) break;
            if (removed.equals(userIdString)) {
                seen(couponId).remove(userIdString);
                throw new OutOfStockException();
            }
        }

    }

    @Override
    public void freeSlot(Long couponId, Long userId) {
        queue(couponId).remove(userId);
        seen(couponId).remove(userId.toString());
    }

    @Override
    public void markIssued(Long couponId, Long userId) {
        addIssuedWithTtl(couponId, userId.toString());
    }
}
