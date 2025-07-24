package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.db.UserCouponDB;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponStoreImpl implements UserCouponStore {

    private final UserCouponDB userCouponDB;

    @Override
    public UserCoupon store(Coupon coupon, Long userId) {
        UserCoupon userCoupon = UserCoupon.builder()
                .id(UserCouponDB.getAtomicInteger())
                .userId(userId)
                .couponId(coupon.getId())
                .build();
        return userCouponDB.insert(userCoupon);
    }

    @Override
    public UserCoupon store(UserCoupon userCoupon) {
        return userCouponDB.insert(userCoupon);
    }
}
