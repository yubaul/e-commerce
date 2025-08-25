package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.domain.order.orderinfo.OrderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    @Async("orderEventExecutor")
    @EventListener
    public void handle(OrderInfo.Order orderInfo){
        log.info("데이터 플랫폼 전송 이벤트 처리 중 (스레드: {})", Thread.currentThread().getName());
    }

}
