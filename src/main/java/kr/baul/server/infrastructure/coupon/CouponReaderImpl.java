package kr.baul.server.infrastructure.coupon;

import kr.baul.server.common.exception.EntityNotFoundException;
import kr.baul.server.db.CouponDB;
import kr.baul.server.domain.coupon.Coupon;
import kr.baul.server.domain.coupon.CouponReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponReaderImpl implements CouponReader{

    private final CouponDB couponDB;

    @Override
    public Coupon getCoupon(Long couponId) {
        return couponDB.selectById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 쿠폰이 존재하지 않습니다."));
    }
}
