package kr.baul.server.infrastructure;

import kr.baul.server.domain.notification.NotificationService;
import kr.baul.server.domain.order.orderinfo.OrderInfo;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyOrderCompleted(OrderInfo.Order orderInfo) {
        // 외부 데이터 플랫폼으로의 전송 기능!!
        // Mock 구현체입니다 !!
    }
}
