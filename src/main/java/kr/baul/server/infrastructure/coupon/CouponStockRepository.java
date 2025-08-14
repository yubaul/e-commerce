package kr.baul.server.infrastructure.coupon;
import kr.baul.server.domain.coupon.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    Optional<CouponStock> findByCouponId(Long couponId);
}
