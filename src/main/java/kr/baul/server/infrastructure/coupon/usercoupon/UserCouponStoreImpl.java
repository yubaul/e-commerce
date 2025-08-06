package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponStoreImpl implements UserCouponStore {

    private final UserCouponRepository userCouponRepository;

    @Override
    public UserCoupon store(Long couponId, Long userId) {
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();
        return userCouponRepository.save(userCoupon);
    }

    @Override
    public UserCoupon store(UserCoupon userCoupon) {
        return userCouponRepository.save(userCoupon);
    }
}
