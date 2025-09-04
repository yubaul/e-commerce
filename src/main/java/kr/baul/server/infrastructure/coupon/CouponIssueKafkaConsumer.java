package kr.baul.server.infrastructure.coupon;

import kr.baul.server.application.coupon.CouponService;
import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.domain.coupon.CouponCommand;
import kr.baul.server.domain.ouxbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static kr.baul.server.common.JsonMapper.fromJson;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueKafkaConsumer {

    private final OutboxService outboxService;
    private final CouponService couponService;

    @KafkaListener(
            topics = KafkaConstant.COUPON_ISSUE,
            groupId = KafkaConstant.COUPON_ISSUE_GROUP,
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCouponIssued(String message, Acknowledgment ack){
        try {
            // 선착순 쿠폰 발급
            var command = fromJson(message, CouponCommand.IssueCoupon.class);
            couponService.finalizeCouponIssue(command.getCouponId(), command.getUserId());

            // Outbox 상태 COMPLETED 처리
            var aggregateId = command.getCouponId() + "-" + command.getUserId();
            outboxService.markCompleted(KafkaConstant.COUPON_ISSUE, aggregateId);

            // 오프셋 커밋
            ack.acknowledge();

            log.info("쿠폰 발급 이벤트 정상 처리 완료: couponId={}, userId={}, partition-thread={}",
                    command.getCouponId(), command.getUserId(), Thread.currentThread().getName());

        } catch (Exception e) {
            log.error("메시지 처리 실패: message={}, err={}", message, e.toString());
            throw e;
        }
    }

}
