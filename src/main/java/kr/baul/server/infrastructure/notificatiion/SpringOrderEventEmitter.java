package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.domain.notification.OrderEventEmitter;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringOrderEventEmitter implements OrderEventEmitter {

    private final ApplicationEventPublisher publisher;

    @Override
    public void orderCompleted(OrderInfo.Order orderInfo) {
        publisher.publishEvent(orderInfo);
    }
}
