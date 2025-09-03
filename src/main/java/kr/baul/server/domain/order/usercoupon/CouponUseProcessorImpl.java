package kr.baul.server.domain.order.usercoupon;

import kr.baul.server.common.config.lock.CommonLock;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponUseProcessorImpl implements CouponUseProcessor{

    private final UserCouponReader userCouponReader;
    private final CouponReader couponReader;
    private final UserCouponStore userCouponStore;

    @CommonLock(key = "'userCoupon'", ids = {"#couponId", "#userId"})
    @Override
    public Coupon reserve(Long couponId, Long userId, Long orderId) {
        UserCoupon userCoupon = userCouponReader.getUserCoupon(couponId, userId);
        userCoupon.hold(orderId);
        userCouponStore.store(userCoupon);
        return couponReader.getCoupon(userCoupon.getCouponId());
    }

    @CommonLock(key = "'userCoupon'", ids = {"#couponId", "#userId"})
    @Override
    public boolean use(Long couponId, Long userId, Long orderId) {
        return userCouponReader.getUserCouponBoundToOrder(couponId, userId, orderId)
                .map( userCoupon ->{
                    userCoupon.confirmUse(orderId);
                    userCouponStore.store(userCoupon);
                    return true;
                })
                .orElse(false);
    }

    @CommonLock(key = "'userCoupon'", ids = {"#couponId", "#userId"})
    @Override
    public boolean cancelReserve(Long couponId, Long userId, Long orderId) {
        return userCouponReader.getUserCouponBoundToOrder(couponId, userId, orderId)
                .map( userCoupon ->{
                    userCoupon.release(orderId);
                    userCouponStore.store(userCoupon);
                    return true;
                })
                .orElse(false);
    }
}
