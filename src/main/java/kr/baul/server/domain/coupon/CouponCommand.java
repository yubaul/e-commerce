package kr.baul.server.domain.coupon;

import kr.baul.server.common.JsonMapper;
import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.common.constants.OutboxEventConstant;
import kr.baul.server.domain.ouxbox.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record CouponCommand()
{
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueCoupon{
        private Long userId;
        private Long couponId;

        public OutboxEvent toOutboxEventEntity(){
            String aggregateId = couponId + "-" + userId;
            return OutboxEvent.builder()
                    .topic(KafkaConstant.COUPON_ISSUE)
                    .aggregateId(aggregateId)
                    .type(OutboxEventConstant.TYPE_COUPON_ISSUED)
                    .status(OutboxEvent.OutboxEventStatus.PENDING)
                    .payload(JsonMapper.toJson(this))
                    .occurredAt(LocalDateTime.now())
                    .build();
        }
    }
}
