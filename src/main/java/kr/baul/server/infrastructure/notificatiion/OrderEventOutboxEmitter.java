package kr.baul.server.infrastructure.notificatiion;

import kr.baul.server.domain.ouxbox.OutboxEvent;
import kr.baul.server.domain.ouxbox.OutboxEventStore;
import kr.baul.server.domain.notification.OrderEventEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventOutboxEmitter implements OrderEventEmitter {

    private final OutboxEventStore outboxEventStore;

    @Override
    public void orderCompleted(OutboxEvent event) {
        outboxEventStore.store(event);
    }

}
