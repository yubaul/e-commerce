package kr.baul.server.application.coupon;


import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponReader userCouponReader;
    private final UserCouponStore userCouponStore;
    private final UserCouponDetailMapper userCouponDetailMapper;
    private final CouponStockReader couponStockReader;
    private final CouponStockStore couponStockStore;
    private static final int COUPON_ISSUE_DECREMENT = 1;

    @Transactional
    public void issueCouponToUser(CouponCommand.IssueCoupon command){
        var userId = command.userId();
        var couponId = command.couponId();

        CouponStock couponStock = couponStockReader.getCouponStockWithLock(couponId);
        couponStock.decrease(COUPON_ISSUE_DECREMENT);

        couponStockStore.store(couponStock);

        try {
            userCouponStore.store(couponStock.getCouponId(), userId);
        }catch (DataIntegrityViolationException e){
            throw new DuplicateCouponIssueException();
        }

    }

    @Transactional(readOnly = true)
    public List<UserCouponDetail.UserCouponInfo> getUserCoupons(Long userId){
        List<UserCoupon> userCoupons = userCouponReader.getUserCoupons(userId);
        return userCouponDetailMapper.of(userCoupons);
    }

}
