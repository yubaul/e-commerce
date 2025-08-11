package kr.baul.server.infrastructure.coupon;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.domain.coupon.CouponStock;
import kr.baul.server.domain.coupon.CouponStockReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponStockReaderImpl implements CouponStockReader {
    private final CouponStockRepository couponStockRepository;

    @Override
    public CouponStock getCouponStockWithLock(Long couponId) {
        return couponStockRepository.findForUpdateByCouponId(couponId)
                .orElseThrow(() -> new EntityNotFoundException("해당 쿠폰에 재고 엔티티가 없습니다."));
    }
}
