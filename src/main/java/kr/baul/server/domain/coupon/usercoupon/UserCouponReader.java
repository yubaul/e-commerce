package kr.baul.server.domain.coupon.usercoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponReader {

    List<UserCoupon> getUserCoupons(Long userId);

    UserCoupon getUserCoupon(Long couponId, Long userId);

    Optional<UserCoupon> getUserCouponBoundToOrder(Long couponId, Long userId, Long orderId);
}
