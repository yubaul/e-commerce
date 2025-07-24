package kr.baul.server.domain.notification;

import kr.baul.server.domain.order.orderinfo.OrderInfo;

public interface NotificationService {
    void notifyOrderCompleted(OrderInfo.Order orderInfo);
}
