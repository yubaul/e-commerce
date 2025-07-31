package kr.hhplus.be.server.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.domain.coupon.UserCouponDetail;
import kr.baul.server.interfaces.coupon.CouponDto;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        classes = kr.baul.server.ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class CouponServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    CouponService couponService;

    @Test
    void 이미_쿠폰을_보유하고_있으면_예외() {
        // given
        Long userId = 10L;
        Long couponId = 200L;

        CouponDto.CouponIssueRequest request = new CouponDto.CouponIssueRequest();
        request.setUserId(userId);
        request.setCouponId(couponId);

        // expect
        assertThatThrownBy(() -> couponService.issueCouponToUser(request))
                .isInstanceOf(DuplicateCouponIssueException.class);
    }

    @Test
    void 쿠폰을_정상_발급() {
        // given
        Long userId = 10L;
        Long couponId = 201L;

        CouponDto.CouponIssueRequest request = new CouponDto.CouponIssueRequest();
        request.setUserId(userId);
        request.setCouponId(couponId);

        // when
        couponService.issueCouponToUser(request);

        // then
        List<UserCouponDetail.UserCouponInfo> result = couponService.getUserCoupons(userId);
        assertThat(result).anyMatch(info -> info.getCouponId().equals(couponId));
    }

    @Test
    void 유저_쿠폰을_조회하고_매핑해서_반환한다() {
        // given
        Long userId = 10L;

        // when
        List<UserCouponDetail.UserCouponInfo> result = couponService.getUserCoupons(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }
}