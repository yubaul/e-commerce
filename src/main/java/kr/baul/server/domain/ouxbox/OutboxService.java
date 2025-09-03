package kr.baul.server.domain.ouxbox;

import kr.baul.server.common.config.lock.CommonLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventReader outboxEventReader;
    private final OutboxEventStore outboxEventStore;

    @CommonLock(key = "outbox", ids = {"#topic", "#eventId"})
    public void markPublished(String topic, String aggregateId) {
        OutboxEvent event = outboxEventReader.getOutboxEvent(topic, aggregateId);
        event.markPublished();
        outboxEventStore.store(event);
    }

    @CommonLock(key = "outbox", ids = {"#topic", "#eventId"})
    public void markFailed(String topic, String aggregateId, String error) {
        OutboxEvent event = outboxEventReader.getOutboxEvent(topic, aggregateId);
        event.markFailed(error);
        outboxEventStore.store(event);
    }

    @CommonLock(key = "outbox", ids = {"#topic", "#eventId"})
    public void markCompleted(String topic, String aggregateId) {
        OutboxEvent event = outboxEventReader.getOutboxEvent(topic, aggregateId);
        event.markCompleted();
        outboxEventStore.store(event);
    }

}
