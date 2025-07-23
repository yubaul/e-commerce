package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.db.UserCouponDB;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserCouponReaderImpl implements UserCouponReader {

    private final UserCouponDB couponDB;

    @Override
    public boolean hasCoupon(Long userId, Long couponId) {
        return couponDB.selectByUserId(userId)
                .map(list -> list.stream()
                        .anyMatch(c -> Objects.equals(c.getCouponId(), couponId)))
                .orElse(false);
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId) {
        return couponDB.selectByUserId(userId)
                .orElse(Collections.emptyList());
    }
}
