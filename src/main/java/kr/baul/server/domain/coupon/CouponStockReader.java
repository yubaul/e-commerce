package kr.baul.server.domain.coupon;

public interface CouponStockReader {
    CouponStock getCouponStockWithLock(Long couponId);
}
