package kr.baul.server.infrastructure.coupon;

import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponStoreImpl implements CouponStore {

   private final CouponRepository couponRepository;

    @Override
    public Coupon store(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
