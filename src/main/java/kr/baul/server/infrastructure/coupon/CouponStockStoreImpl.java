package kr.baul.server.infrastructure.coupon;

import kr.baul.server.domain.coupon.CouponStock;
import kr.baul.server.domain.coupon.CouponStockStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponStockStoreImpl implements CouponStockStore {

    private final CouponStockRepository couponStockRepository;

    @Override
    public CouponStock store(CouponStock couponStock) {
        return couponStockRepository.save(couponStock);
    }
}
