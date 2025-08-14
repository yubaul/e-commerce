package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCouponReaderImpl implements UserCouponReader {

    private final UserCouponRepository userCouponRepository;

    @Override
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findAllByUserIdAndUserCouponStatus(userId, UserCoupon.UserCouponStatus.AVAILABLE);
    }

    @Override
    public UserCoupon getUserCoupon(Long couponId, Long userId) {
        return userCouponRepository.findByCouponIdAndUserId(couponId, userId)
                .orElseThrow(() -> new EntityNotFoundException("보유 쿠폰 목록에 쿠폰이 존재하지 않습니다."));
    }

    @Override
    public Optional<UserCoupon> getUserCouponBoundToOrder(Long couponId, Long userId, Long orderId) {
        return userCouponRepository.findByCouponIdAndUserIdAndOrderId(couponId, userId, orderId);
    }
}
