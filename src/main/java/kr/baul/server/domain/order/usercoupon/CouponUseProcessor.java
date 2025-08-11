package kr.baul.server.domain.order.usercoupon;

import kr.baul.server.domain.coupon.Coupon;

public interface CouponUseProcessor {
    Coupon useCoupon(Long couponId, Long userId);
}
