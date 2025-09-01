package kr.baul.server.application.order;

import kr.baul.server.domain.notification.NotificationService;
import kr.baul.server.domain.order.OrderCommand;
import kr.baul.server.domain.order.OrderService;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final NotificationService notificationService;
    private final OrderInfoMapper orderInfoMapper;

    public void registerOrder(OrderCommand.RegisterOrder registerOrder){
        var orderInfo = orderService.registerOrder(registerOrder);
        var orderCompleted = orderInfoMapper.of(orderInfo);
        notificationService.notifyOrderCompleted(orderCompleted);
    }
}
