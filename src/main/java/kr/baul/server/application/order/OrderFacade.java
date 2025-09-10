package kr.baul.server.application.order;


import kr.baul.server.domain.order.OrderCommand;
import kr.baul.server.domain.order.OrderService;
import kr.baul.server.domain.order.orderinfo.OrderInfoMapper;
import kr.baul.server.domain.ouxbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final OrderInfoMapper orderInfoMapper;
    private final OutboxService outboxService;

    public void registerOrder(OrderCommand.RegisterOrder registerOrder){
        var orderInfo = orderService.registerOrder(registerOrder);
        var orderCompleted = orderInfoMapper.of(orderInfo);
        var outboxEvent = orderCompleted.toOutboxEventEntity();
        outboxService.save(outboxEvent);
    }
}
