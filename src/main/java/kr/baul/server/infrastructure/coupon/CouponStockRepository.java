package kr.baul.server.infrastructure.coupon;
import jakarta.persistence.LockModeType;
import kr.baul.server.domain.coupon.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {

    Optional<CouponStock> findByCouponId(Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CouponStock> findForUpdateByCouponId(Long couponId);
}
