package kr.baul.server.application.coupon;


import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
import kr.baul.server.interfaces.coupon.CouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponReader userCouponReader;
    private final UserCouponStore userCouponStore;
    private final CouponReader couponReader;
    private final CouponStore couponStore;
    private final UserCouponDetailMapper userCouponDetailMapper;

    public void issueCouponToUser(CouponDto.CouponIssueRequest request){
        var userId = request.getUserId();
        var couponId = request.getCouponId();

        if (userCouponReader.hasCoupon(userId, couponId)) {
            throw new DuplicateCouponIssueException();
        }

        Coupon coupon = couponReader.getCoupon(couponId);
        coupon.useOne();

        couponStore.store(coupon);
        userCouponStore.store(coupon, userId);
    }

    public List<UserCouponDetail.UserCouponInfo> getUserCoupons(Long userId){
        List<UserCoupon> userCoupons = userCouponReader.getUserCoupons(userId);
        return userCouponDetailMapper.of(userCoupons);
    }

}
