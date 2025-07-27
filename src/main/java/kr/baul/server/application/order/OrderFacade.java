package kr.baul.server.application.order;

import kr.baul.server.domain.notification.NotificationService;
import kr.baul.server.domain.order.OrderCommand;
import kr.baul.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final NotificationService notificationService;

    public void registerOrder(OrderCommand.RegisterOrder registerOrder){
        var orderInfo = orderService.registerOrder(registerOrder);
        notificationService.notifyOrderCompleted(orderInfo);
    }
}
