package kr.baul.server.domain.coupon.issuing;

import kr.baul.server.common.config.lock.CommonLock;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.domain.coupon.CouponStock;
import kr.baul.server.domain.coupon.CouponStockReader;
import kr.baul.server.domain.coupon.CouponStockStore;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFinalizerImpl implements CouponFinalizer{

    private final CouponStockReader couponStockReader;
    private final CouponStockStore couponStockStore;
    private final UserCouponStore userCouponStore;

    private static final int COUPON_ISSUE_DECREMENT = 1;

    @CommonLock(key = "coupon", id = "#couponId")
    @Override
    public void finalizeIssue(Long couponId, Long userId) {
        CouponStock couponStock = couponStockReader.getCouponStock(couponId);
        couponStock.decrease(COUPON_ISSUE_DECREMENT);
        couponStockStore.store(couponStock);

        try {
            userCouponStore.store(couponStock.getCouponId(), userId);
        }catch (DataIntegrityViolationException e){
            throw new DuplicateCouponIssueException();
        }

    }
}
