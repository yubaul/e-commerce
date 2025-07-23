package kr.baul.server.domain.coupon.usercoupon;

import kr.baul.server.domain.coupon.Coupon;

public interface UserCouponStore {
    UserCoupon store(Coupon coupon, Long userId);
}
