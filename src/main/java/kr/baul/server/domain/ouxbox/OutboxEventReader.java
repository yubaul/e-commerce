package kr.baul.server.domain.ouxbox;

import java.util.List;

public interface OutboxEventReader {

    OutboxEvent getOutboxEvent(String topic, String aggregateId);

    OutboxEvent getPendingEvent(String topic, String aggregateId);

    List<OutboxEvent> getPendingEvents(int limit);

}
