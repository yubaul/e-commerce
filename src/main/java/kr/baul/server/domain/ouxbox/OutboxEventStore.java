package kr.baul.server.domain.ouxbox;

public interface OutboxEventStore {

    OutboxEvent store(OutboxEvent outboxEvent);

}
