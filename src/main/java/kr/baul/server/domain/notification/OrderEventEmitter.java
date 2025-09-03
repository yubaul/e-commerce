package kr.baul.server.domain.notification;

import kr.baul.server.domain.ouxbox.OutboxEvent;

public interface OrderEventEmitter {

    void orderCompleted(OutboxEvent event);

}
