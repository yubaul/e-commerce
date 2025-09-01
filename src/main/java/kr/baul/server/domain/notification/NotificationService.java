package kr.baul.server.domain.notification;

import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final OrderEventEmitter orderEventEmitter;

    public void notifyOrderCompleted(OrderInfo.Order orderInfo){
        orderEventEmitter.orderCompleted(orderInfo);
    }

}
