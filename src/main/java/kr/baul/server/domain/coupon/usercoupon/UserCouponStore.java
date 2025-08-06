package kr.baul.server.domain.coupon.usercoupon;

public interface UserCouponStore {
    UserCoupon store(Long couponId, Long userId);
    UserCoupon store(UserCoupon userCoupon);

}
