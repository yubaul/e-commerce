package kr.baul.server.infrastructure.coupon.usercoupon;

import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findAllByUserIdAndUsedFalse(Long userId);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);
}
