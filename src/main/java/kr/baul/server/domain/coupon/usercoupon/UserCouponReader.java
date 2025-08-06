package kr.baul.server.domain.coupon.usercoupon;

import java.util.List;

public interface UserCouponReader {

    List<UserCoupon> getUserCoupons(Long userId);

    UserCoupon getUserCoupon(Long couponId, Long userId);
}
