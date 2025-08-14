package kr.baul.server.domain.order.usercoupon;

import kr.baul.server.domain.coupon.Coupon;

public interface CouponUseProcessor {

    Coupon reserve(Long couponId, Long userId, Long orderId);

    boolean use(Long couponId, Long userId, Long orderId);

    boolean cancelReserve(Long couponId, Long userId, Long orderId);
}
