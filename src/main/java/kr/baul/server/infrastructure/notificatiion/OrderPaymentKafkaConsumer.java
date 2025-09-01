package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentKafkaConsumer {

    @KafkaListener(
            topics = KafkaConstant.ORDER_PAYMENT,
            groupId = KafkaConstant.DATA_PLATFORM_GROUP,
            concurrency = "3"
    )
    public void consume(OrderInfo.OrderCompleted orderCompleted){
        log.info(orderCompleted.id().toString());
        log.info(orderCompleted.userId().toString());
        log.info(orderCompleted.totalAmount().toString());
        log.info("데이터 플랫폼 이벤트 처리 중 (스레드: {})", Thread.currentThread().getName());
        log.info("데이터 플랫폼 이벤트 처리 중 (스레드: {}), (message: {})", Thread.currentThread().getName(), orderCompleted);
    }

}
