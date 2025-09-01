package kr.baul.server.domain.notification;

import kr.baul.server.domain.order.orderinfo.OrderInfo;

public interface OrderEventEmitter {

    void orderCompleted(OrderInfo.OrderCompleted orderCompleted);
}
