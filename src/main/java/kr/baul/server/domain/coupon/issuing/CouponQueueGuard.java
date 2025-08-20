package kr.baul.server.domain.coupon.issuing;

public interface CouponQueueGuard {
    void assertNotIssued(Long couponId, Long userId);

    void tryEnter(Long couponId, Long userId);

    void freeSlot(Long couponId, Long userId);

    void markIssued(Long couponId, Long userId);
}
