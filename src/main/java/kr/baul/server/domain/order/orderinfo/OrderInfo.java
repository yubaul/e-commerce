package kr.baul.server.domain.order.orderinfo;

import kr.baul.server.common.JsonMapper;
import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.common.constants.OutboxEventConstant;
import kr.baul.server.domain.ouxbox.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class OrderInfo {

    @Builder
    public record Order(
            Long id,
            Long userId,
            Long totalAmount,
            LocalDateTime createdAt
    ){

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderCompleted {
        private Long id;
        private Long userId;
        private Long totalAmount;

        public OutboxEvent toOutboxEventEntity(){
            return OutboxEvent.builder()
                    .topic(KafkaConstant.ORDER_PAYMENT)
                    .aggregateId(id.toString())
                    .type(OutboxEventConstant.TYPE_ORDER_PAYMENT_COMPLETED)
                    .status(OutboxEvent.OutboxEventStatus.PENDING)
                    .payload(JsonMapper.toJson(this))
                    .occurredAt(LocalDateTime.now())
                    .build();
        }
    }

}
