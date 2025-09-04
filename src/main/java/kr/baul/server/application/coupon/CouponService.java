package kr.baul.server.application.coupon;

import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.issuing.CouponFinalizer;
import kr.baul.server.domain.coupon.issuing.CouponQueueGuard;
import kr.baul.server.domain.coupon.usercoupon.*;
import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxService;
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
    private final OutboxService outboxService;

    public void issueCouponToUser(CouponCommand.IssueCoupon command){
        var userId = command.getUserId();
        var couponId = command.getCouponId();

        // 1) 이미 발급된 유저면 종료
        queueGuard.assertNotIssued(couponId, userId);

        // 2) 대기열 입장 시도
        queueGuard.tryEnter(couponId, userId);

        // 3) 아웃박스 생성 및 이벤트 발행
        OutboxEvent event = command.toOutboxEventEntity();
        outboxService.save(event);

    }

    public void finalizeCouponIssue(Long couponId, Long userId){
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
