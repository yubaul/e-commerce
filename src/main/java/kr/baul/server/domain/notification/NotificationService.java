package kr.baul.server.domain.notification;

import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final OrderEventEmitter orderEventEmitter;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void notifyOrderCompleted(OrderInfo.OrderCompleted orderCompleted){
        var outboxEvent = orderCompleted.toOutboxEventEntity();
        orderEventEmitter.orderCompleted(outboxEvent);
        publisher.publishEvent(outboxEvent);
    }

}
