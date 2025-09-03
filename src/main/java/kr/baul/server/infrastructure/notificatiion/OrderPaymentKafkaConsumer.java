package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.common.JsonMapper;
import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import kr.baul.server.domain.ouxbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentKafkaConsumer {

    private final OutboxService outboxService;

    @KafkaListener(
            topics = KafkaConstant.ORDER_PAYMENT,
            groupId = KafkaConstant.DATA_PLATFORM_GROUP,
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String message, Acknowledgment ack){
        try {
            var orderCompleted = JsonMapper.fromJson(message, OrderInfo.OrderCompleted.class);

            log.info("소비된 이벤트: id={}, userId={}, totalAmount={}, thread={}",
                    orderCompleted.getId(), orderCompleted.getUserId(),
                    orderCompleted.getTotalAmount(), Thread.currentThread().getName());

            // Outbox 상태 COMPLETED 처리
            outboxService.markCompleted(KafkaConstant.ORDER_PAYMENT, orderCompleted.getId().toString());

            // 오프셋 커밋
            ack.acknowledge();

        } catch (Exception e) {
            log.error("메시지 처리 실패: message={}, err={}", message, e.toString());
            throw e;
        }

    }

}
