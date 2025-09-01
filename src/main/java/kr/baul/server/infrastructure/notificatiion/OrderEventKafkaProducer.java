package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.common.constants.KafkaConstant;
import kr.baul.server.domain.notification.OrderEventEmitter;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventKafkaProducer implements OrderEventEmitter {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void orderCompleted(OrderInfo.OrderCompleted orderCompleted) {
        kafkaTemplate.send(KafkaConstant.ORDER_PAYMENT, orderCompleted);
    }
}
