package kr.baul.server.domain.coupon.usercoupon;

import java.util.List;

public interface UserCouponReader {

    boolean hasCoupon(Long userId, Long couponId);

    List<UserCoupon> getUserCoupons(Long userId);

}
