package kr.hhplus.be.server.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.issuing.CouponFinalizer;
import kr.baul.server.domain.coupon.issuing.CouponQueueGuard;
import kr.baul.server.domain.coupon.usercoupon.*;
import kr.baul.server.domain.ouxbox.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

    @InjectMocks
    CouponService couponService;

    @Mock UserCouponReader userCouponReader;
    @Mock CouponFinalizer couponFinalizer;
    @Mock CouponQueueGuard couponQueueGuard;
    @Mock UserCouponDetailMapper userCouponDetailMapper;
    @Mock OutboxService outboxService;

    @Test
    void 이미_쿠폰을_보유하고_있으면_예외() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        var command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        doThrow(new DuplicateCouponIssueException())
                .when(couponQueueGuard).assertNotIssued(couponId, userId);

        // when & then
        assertThatThrownBy(() -> couponService.issueCouponToUser(command))
                .isInstanceOf(DuplicateCouponIssueException.class);

        // then
        verify(couponQueueGuard).assertNotIssued(couponId, userId);
        verifyNoMoreInteractions(couponFinalizer);
        verify(couponQueueGuard, never()).tryEnter(any(), any());
        verify(couponQueueGuard, never()).freeSlot(any(), any());
        verify(couponQueueGuard, never()).markIssued(any(), any());
    }


    @Test
    void 쿠폰을_정상_발급() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        // when
        couponService.finalizeCouponIssue(couponId, userId);

        // then
        InOrder inOrder = inOrder(couponFinalizer, couponQueueGuard);
        inOrder.verify(couponFinalizer).finalizeIssue(couponId, userId);
        inOrder.verify(couponQueueGuard).markIssued(couponId, userId);

        // 보상 호출 없어야 함
        verify(couponQueueGuard, never()).freeSlot(any(), any());
    }

    @Test
    void 최종_확정_실패시_슬롯_복구하고_예외_전파() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        doThrow(new OutOfStockException())
                .when(couponFinalizer).finalizeIssue(couponId, userId);

        // when & then
        assertThatThrownBy(() -> couponService.finalizeCouponIssue(couponId, userId))
                .isInstanceOf(OutOfStockException.class);

        // then
        verify(couponFinalizer).finalizeIssue(couponId, userId);
        verify(couponQueueGuard).freeSlot(couponId, userId);
        verify(couponQueueGuard, never()).markIssued(any(), any());
    }

    @Test
    void 유저_쿠폰을_조회하고_매핑해서_반환한다() {
        // given
        Long userId = 1L;
        List<UserCoupon> userCoupons = List.of(mock(UserCoupon.class));
        List<UserCouponDetail.UserCouponInfo> mapped = List.of(mock(UserCouponDetail.UserCouponInfo.class));

        when(userCouponReader.getUserCoupons(userId)).thenReturn(userCoupons);
        when(userCouponDetailMapper.of(userCoupons)).thenReturn(mapped);

        // when
        List<UserCouponDetail.UserCouponInfo> result = couponService.getUserCoupons(userId);

        // then
        assertThat(result).isEqualTo(mapped);
    }
}