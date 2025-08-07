package kr.baul.server.domain.order.usercoupon;

import jakarta.persistence.OptimisticLockException;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponUseProcessorImpl implements CouponUseProcessor{

    private final UserCouponReader userCouponReader;
    private final CouponReader couponReader;

    @Override
    @Retryable(
            value = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class,
                    OptimisticLockingFailureException.class
            },
            maxAttempts = 4,
            backoff = @Backoff(delay = 100, multiplier = 1.5)
    )
    public Coupon useCoupon(Long couponId, Long userId) {
        UserCoupon userCoupon = userCouponReader.getUserCoupon(couponId, userId);
        userCoupon.use();
        Coupon coupon = couponReader.getCoupon(userCoupon.getCouponId());
        return coupon;
    }
}
