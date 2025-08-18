package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findAllByUserIdAndUserCouponStatus(Long userId, UserCoupon.UserCouponStatus userCouponStatus);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);

    Optional<UserCoupon> findByCouponIdAndUserIdAndOrderId(Long couponId, Long userId, Long orderId);
}
