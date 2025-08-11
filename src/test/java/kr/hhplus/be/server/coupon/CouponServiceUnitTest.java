package kr.hhplus.be.server.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.domain.coupon.*;
import kr.baul.server.domain.coupon.usercoupon.UserCoupon;
import kr.baul.server.domain.coupon.usercoupon.UserCouponReader;
import kr.baul.server.domain.coupon.usercoupon.UserCouponStore;
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
    @Mock UserCouponStore userCouponStore;
    @Mock CouponReader couponReader;
    @Mock CouponStore couponStore;
    @Mock UserCouponDetailMapper userCouponDetailMapper;

    @Mock CouponStockReader couponStockReader;
    @Mock CouponStockStore couponStockStore;

    @Test
    void 이미_쿠폰을_보유하고_있으면_예외() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        CouponStock mockStock = mock(CouponStock.class);
        when(couponStockReader.getCouponStockWithLock(couponId)).thenReturn(mockStock);
        when(mockStock.getCouponId()).thenReturn(couponId);
        doThrow(new DuplicateCouponIssueException()).when(userCouponStore).store(couponId, userId);

        // when&then
        assertThatThrownBy(() -> couponService.issueCouponToUser(command))
                .isInstanceOf(DuplicateCouponIssueException.class);
    }

    @Test
    void 쿠폰을_정상_발급() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        CouponStock couponStock = mock(CouponStock.class);

        when(couponStockReader.getCouponStockWithLock(couponId)).thenReturn(couponStock);

        // when
        couponService.issueCouponToUser(command);

        // then
        // 예외 없으면 성공
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