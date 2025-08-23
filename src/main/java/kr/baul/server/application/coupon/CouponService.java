package kr.baul.server.application.coupon;

import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.issuing.CouponFinalizer;
import kr.baul.server.domain.coupon.issuing.CouponQueueGuard;
import kr.baul.server.domain.coupon.usercoupon.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponReader userCouponReader;
    private final UserCouponDetailMapper userCouponDetailMapper;
    private final CouponFinalizer couponFinalizer;
    private final CouponQueueGuard queueGuard;

    public void issueCouponToUser(CouponCommand.IssueCoupon command){
        var userId = command.userId();
        var couponId = command.couponId();

        // 1) 이미 발급된 유저면 종료
        queueGuard.assertNotIssued(couponId, userId);

        // 2) 대기열 입장 시도
        queueGuard.tryEnter(couponId, userId);

        // 3) 최종 확정 (실패 시 슬롯 복구)
        try {
            couponFinalizer.finalizeIssue(couponId, userId);
            queueGuard.markIssued(couponId, userId);
        } catch (RuntimeException e){
            queueGuard.freeSlot(couponId, userId);
            throw e;
        }

    }

    @Transactional(readOnly = true)
    public List<UserCouponDetail.UserCouponInfo> getUserCoupons(Long userId){
        List<UserCoupon> userCoupons = userCouponReader.getUserCoupons(userId);
        return userCouponDetailMapper.of(userCoupons);
    }

}
