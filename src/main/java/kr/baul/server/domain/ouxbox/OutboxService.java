package kr.baul.server.domain.ouxbox;

import kr.baul.server.common.config.lock.CommonLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventReader outboxEventReader;
    private final OutboxEventStore outboxEventStore;
    private final ApplicationEventPublisher publisher;

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

    @Transactional
    public void save(OutboxEvent event){
        outboxEventStore.store(event);
        publisher.publishEvent(event);
    }

}
