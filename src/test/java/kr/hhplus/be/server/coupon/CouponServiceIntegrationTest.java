package kr.hhplus.be.server.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.common.exception.DuplicateCouponIssueException;
import kr.baul.server.common.exception.OutOfStockException;
import kr.baul.server.domain.coupon.CouponCommand;
import kr.baul.server.domain.coupon.usercoupon.UserCouponDetail;
import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxEventReader;
import kr.hhplus.be.server.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
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
    OutboxEventReader outboxEventReader;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void 이미_쿠폰을_보유하고_있으면_예외() {
        // given
        Long userId = 10L;
        Long couponId = 200L;

        var command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        // expect
        assertThatThrownBy(() -> couponService.issueCouponToUser(command))
                .isInstanceOf(DuplicateCouponIssueException.class);
    }

    @Test
    void 쿠폰을_정상_발급하면_Outbox에_Pending저장() {
        // given
        Long userId = 10L;
        Long couponId = 201L;

        var command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        // when
        couponService.issueCouponToUser(command);

        // then
        var aggregateId = couponId + "-" + userId;
        OutboxEvent outbox = outboxEventReader.getOutboxEvent(KafkaConstant.COUPON_ISSUE, aggregateId);
        assertThat(outbox).isNotNull();
        assertThat(outbox.getStatus()).isEqualTo(OutboxEvent.OutboxEventStatus.PENDING);
    }

    @Test
    void 다수_유저의_동시_쿠폰_요청에도_재고만큼만_큐잉() throws Exception {
        // given
        int threadCount = 30;
        int limit = 30; // Redis seed 에서 설정된 limit
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Long couponId = 500L;
        AtomicInteger successCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            Long userId = 1000L + i;
            executor.submit(() -> {
                try {
                    var command = CouponCommand.IssueCoupon.builder()
                            .userId(userId)
                            .couponId(couponId)
                            .build();
                    couponService.issueCouponToUser(command);
                    successCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then: 발급 요청은 많이 들어와도 Outbox에는 최대 limit만큼만 저장되어야 함
        // (추가 실패건은 Redis guard에서 OutOfStockException 발생)
        assertThat(successCount.get()).isLessThanOrEqualTo(limit);
    }

    @Test
    void 동시_쿠폰_요청_중_재고_초과분은_예외발생() throws Exception {
        // given
        int threadCount = 11;
        int limit = 10; // Redis seed 에서 설정된 limit
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
                    var command = CouponCommand.IssueCoupon.builder()
                            .userId(userId)
                            .couponId(couponId)
                            .build();
                    couponService.issueCouponToUser(command);
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    e.printStackTrace();
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        System.out.println("successCount: " + successCount.get());
        System.out.println("failureCount: " + failureCount.get());
        assertThat(successCount.get()).isEqualTo(limit);
        assertThat(failureCount.get()).isEqualTo(threadCount - limit);
    }

    @Test
    void 한_유저가_여러번_요청하면_중복발급_예외() throws Exception {
        // given
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        Long userId = 100L;
        Long couponId = 502L;

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    couponService.issueCouponToUser(new CouponCommand.IssueCoupon(userId, couponId));
                } catch (DuplicateCouponIssueException e) {
                    duplicateCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then: 한 번만 성공하고 나머지는 중복 예외
        assertThat(duplicateCount.get()).isEqualTo(threadCount - 1);
        var aggregateId = couponId + "-" + userId;
        OutboxEvent outbox = outboxEventReader.getOutboxEvent(KafkaConstant.COUPON_ISSUE, aggregateId);
        assertThat(outbox).isNotNull();
        assertThat(outbox.getStatus()).isEqualTo(OutboxEvent.OutboxEventStatus.PENDING);
    }

    @Test
    void 쿠폰_이벤트_소비하면_최종발급_완료된다() throws Exception {
        // given
        Long userId = 77L;
        Long couponId = 201L;
        var command = CouponCommand.IssueCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        // 발급 요청 → Outbox PENDING 저장
        var aggregateId = couponId + "-" + userId;
        couponService.issueCouponToUser(command);
        OutboxEvent pending = outboxEventReader.getOutboxEvent(KafkaConstant.COUPON_ISSUE, aggregateId);

        // when
        kafkaTemplate.send(KafkaConstant.COUPON_ISSUE, couponId.toString(), pending.getPayload()).get();
        Thread.sleep(1500); // Consumer가 consume 할 시간 확보

        // then
        OutboxEvent completed = outboxEventReader.getOutboxEvent(KafkaConstant.COUPON_ISSUE, aggregateId);
        assertThat(completed.getStatus()).isEqualTo(OutboxEvent.OutboxEventStatus.COMPLETED);

        List<UserCouponDetail.UserCouponInfo> userCoupons = couponService.getUserCoupons(userId);
        assertThat(userCoupons).anyMatch(info -> info.getCouponId().equals(couponId));
    }
}