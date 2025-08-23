package kr.hhplus.be.server.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.domain.coupon.CouponCommand;
import kr.baul.server.domain.coupon.CouponStock;
import kr.baul.server.domain.coupon.usercoupon.UserCouponDetail;
import kr.baul.server.infrastructure.coupon.CouponStockRepository;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        classes = kr.baul.server.ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class CouponServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponStockRepository couponStockRepository;

    @Test
    void 이미_쿠폰을_보유하고_있으면_예외() {
        // given
        Long userId = 10L;
        Long couponId = 200L;

        CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        // expect
        assertThatThrownBy(() -> couponService.issueCouponToUser(command))
                .isInstanceOf(DuplicateCouponIssueException.class);
    }

    @Test
    void 쿠폰을_정상_발급() {
        // given
        Long userId = 10L;
        Long couponId = 201L;

        CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        // when
        couponService.issueCouponToUser(command);

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



    @Test
    void 다수_유저의_동시_쿠폰_요청에도_재고만큼만_정상_발급() throws Exception {
        // given
        int threadCount = 30;
        int initialQuantity = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Long couponId = 500L;
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = 1000L + i;
            executor.submit(() -> {
                try {
                    CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                            .userId(userId)
                            .couponId(couponId)
                            .build();
                    couponService.issueCouponToUser(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        CouponStock couponStock =  couponStockRepository.findById(couponId).get();
        assertThat(couponStock.getQuantity()).isEqualTo(initialQuantity - threadCount);
    }

    @Test
    void 동시_쿠폰_요청_중_재고_초과분_예외_발생() throws Exception {
        // given
        int threadCount = 11;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        Long couponId = 501L;

        // when
        for (int i = 0; i < threadCount; i++) {
            Long userId = (long) (1000 + i);
            executor.submit(() -> {
                try {
                    CouponCommand.IssueCoupon command = CouponCommand.IssueCoupon.builder()
                            .userId(userId)
                            .couponId(couponId)
                            .build();
                    couponService.issueCouponToUser(command);
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failureCount.get()).isEqualTo(1);

        CouponStock updated = couponStockRepository.findById(couponId).get();
        assertThat(updated.getQuantity()).isEqualTo(0);
    }

    @Test
    void 한_유저가_여러번_쿠폰_요청하면_중복_발급_예외가_발생() throws Exception {
        // given
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        Long userId = 100L;
        Long couponId = 502L;

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    couponService.issueCouponToUser(new CouponCommand.IssueCoupon(userId, couponId));
                } catch (DuplicateCouponIssueException e) {
                    e.printStackTrace();
                    duplicateCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // when
        latch.await();

        // then
        int actualIssued = couponService.getUserCoupons(userId).size();

        assertThat(actualIssued)
                .as("최종 발급된 쿠폰은 1개여야 함")
                .isEqualTo(1);

        assertThat(duplicateCount.get())
                .as("나머지는 모두 중복 발급 예외 발생")
                .isEqualTo(threadCount - 1);
    }
}